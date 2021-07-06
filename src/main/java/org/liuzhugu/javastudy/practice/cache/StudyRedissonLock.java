package org.liuzhugu.javastudy.practice.cache;


import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.RedissonLockEntry;
import org.redisson.WriteBehindService;
import org.redisson.api.RFuture;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.client.protocol.Time;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.redisson.connection.ConnectionManager;
import org.redisson.eviction.EvictionScheduler;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StudyRedissonLock {
    private static final ConcurrentMap<String, RedissonLock.ExpirationEntry> EXPIRATION_RENEWAL_MAP = new ConcurrentHashMap();
    protected final ConnectionManager connectionManager;

    protected StudyRedissonLock(Config config) {
        Config configCopy = new Config(config);
        this.connectionManager = ConfigSupport.createConnectionManager(configCopy);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {

        long time = unit.toMillis(waitTime);
        long current = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId();
        //1.尝试获取锁
        Long ttl = tryAcquire(leaseTime,unit,threadId);
        //lock acquire
        if (ttl == null) {
            return true;
        }
        //申请锁的耗时如果大于等于最大等待时间 则申请锁失败
        time -= System.currentTimeMillis() - current;
        if (time <= 0) {
            acquireFailed(threadId);
            return false;
        }
        current = System.currentTimeMillis();
        /**
         * 2.订阅锁释放事件，并通过await方法阻塞等待锁释放   有效地解决了无效的锁释放申请浪费资源的问题：
         *      基于信号量   当锁被其他资源占有时   当前线程通过Redis的channel订阅锁的释放事件   一但锁释放会发消息通知等待
         *      的线程进行竞争
         * 当this.await 返回 false，说明等待时间已经超出获取锁最大等待时间，取消订阅并返回获取锁失败
         * 当this.await 返回 true，进入循环尝试获取锁
         * */
        RFuture<RedissonLockEntry> subscribeFuture = subscribe(threadId);
        //await方法内部是用CountDownLatch来实现阻塞，获取subscribe异步执行的结果(应用Netty 的 Future)
        if (! subscribeFuture.cancel(false)) {
            subscribeFuture.onComplete((res,e) -> {
                if (e == null) {
                    unSubscribe(subscribeFuture,threadId);
                }
            });
            //获取失败
            acquireFailed(threadId);
            return false;
        }
        try {
            //计算获取锁的总耗时，如果大于等于最大等待时间，则获取锁失败
            time = System.currentTimeMillis() - current;
            if (time <= 0) {
                acquireFailed(threadId);
                return false;
            }
            /**
             * 3.收到锁释放的信号后，在最大的等待时间之内，循环一次接着一次地尝试获取锁
             *      获取锁成功，则立马返回true
             *      若在最大时间之内还没获取到锁，则认为获取锁失败，返回false结束循环
             * */
            while (true) {
                long currentTime = System.currentTimeMillis();
                //再次尝试获取锁
                ttl = tryAcquire(leaseTime,unit,threadId);
                //lock acquire
                if (ttl == null) {
                    return true;
                }
                //超过最大等待时间则返回false 结束循环，获取锁失败
                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) {
                    acquireFailed(threadId);
                    return false;
                }
                /**
                 * 6.阻塞等待锁(通过信号量(共享锁)阻塞，等待解锁消息)
                 * */
                if (ttl >= 0 && ttl < time) {
                    //如果剩余时间(ttl)小于wait time，则在ttl时间内  从Entry的信号量获取一个许可(除非被中断或者一直没有可用的许可)
                    getEntry(threadId).getLatch().tryAcquire(time,TimeUnit.MILLISECONDS);
                }
                //更新剩余的等待时间(最大等待时间 - 已经消耗的阻塞时间)
                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) {
                    acquireFailed(threadId);
                    return false;
                }
            }
        } finally {
            //7.无论是否获得锁，都要取消订阅解锁信息
            unSubscribe(subscribeFuture,threadId);
        }
    }


    /**
     * 看门狗模式
     *  实现锁的自动续期
     * */
    //基于线程id定时调度和续期
    private void scheduleExpirationRenewal(long threadId) {
        //如果需要的话新建一个ExpirationEntry记录线程重入计数，同时把续期的任务Timeout对象保存在属性中
        RedissonLock.ExpirationEntry entry = new RedissonLock.ExpirationEntry();
        RedissonLock.ExpirationEntry oldEntry = EXPIRATION_RENEWAL_MAP.putIfAbsent(getEntryName(),entry);
        if (oldEntry != null) {
            //当前进行的线程重入加锁
            oldEntry.addThreadId(threadId);
        } else {
            //当前进行的线程首次加锁
            entry.addThreadId(threadId);
            //首次新建ExpirationEntry需要续期方法，记录续期的任务句柄
            renewExpiration();
        }
    }

    //处理续期
    private void renewExpiration() {
        //根据entryName获取ExpirationEntry实例，如果为空，说明在cancelExpirationRenew()方法已经移除，
        // 一般是解锁的时候释放
        RedissonLock.ExpirationEntry ee = EXPIRATION_RENEWAL_MAP.get(getEntryName());
        if (ee == null) {
            return;
        }
        //新建一个定时任务，这个就是看门狗的实现,io.netty.util.Timeout是Netty结合时间轮使用的定时任务实例
        Timeout task = connectionManager.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                //这里是重复外面的逻辑
                RedissonLock.ExpirationEntry ent = EXPIRATION_RENEWAL_MAP.get(getEntryName());
                if (ent == null) {
                    return;
                }
                //获取ExpirationEntry中的首个线程ID，如果为空说明调用过cancelExpirationRenewal方法清空持有的线程重入计数
                //一般是锁已释放的场景
                Long threadId = ent.getFirstThreadId();
                if (threadId == null) {
                    return;
                }
                //向redis异步发送续期的命令
                RFuture<Boolean> future = renewExpirationAsync(threadId);
                future.onComplete((res,e) -> {
                    //抛出异常，续期失败，只打印日志和终止任务
                    if (e != null) {
                        return;
                    }
                    //返回true证明续期成功，则递归调用续期方法(重新调度自己)，续期失败说明对应的锁已经不存在，直接返回，不在递归
                    if (res) {
                        //递归
                        renewExpiration();
                    }
                });
            }
        },1L,TimeUnit.NANOSECONDS);
    }

    // 调用Redis，执行Lua脚本，进行异步续期
    protected RFuture<Boolean> renewExpirationAsync(long threadId) {
        return connectionManager.getCommandExecutor().evalWriteAsync(getName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN,
                "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
                        "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                        "return 1; " +
                        "end; " +
                        "return 0;",
                Collections.<Object>singletonList(getName()),
                //  这里根据前面的分析，internalLockLeaseTime在leaseTime的值为-1的前提下，对应值为lockWatchdogTimeout
                "", getLockName(threadId));

    }








    private String getEntryName() {
        return null;
    }
    private String getLockName(long threadId) {
        return null;
    }
    private String getName() {
        return null;
    }


    private Long tryAcquire(long leaseTime, TimeUnit unit, long threadId) {
        return null;
    }
    public RFuture<Boolean> tryLockAsync(long waitTime, long leaseTime, TimeUnit unit) {
        return null;
    }

    protected final <V> V get(RFuture<V> future) {
        return null;
    }


    protected RedissonLockEntry getEntry(long threadId) {
        return null;
    }

    private Long acquireFailed(long threadId) {
        return null;
    }

    private RFuture<RedissonLockEntry> subscribe(long threadId) {
        return null;
    }

    private void unSubscribe(RFuture<RedissonLockEntry> subscribeFuture,long threadId) {

    }
}
