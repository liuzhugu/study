package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ConditionVarBlocker implements Blocker {
    private final Lock lock;

    private final Condition condition;

    private final boolean allowAccess2Lock;

    public ConditionVarBlocker(Lock lock) {
        this(lock,true);
    }

    public ConditionVarBlocker(Lock lock, boolean allowAccess2Lock) {
        this.lock = lock;
        this.condition = lock.newCondition();
        this.allowAccess2Lock = allowAccess2Lock;
    }

    public ConditionVarBlocker() {
        this(false);
    }

    public ConditionVarBlocker(boolean allowAccess2Lock) {
        this(new ReentrantLock(),allowAccess2Lock);
    }

    public Lock getLock() {
        if(allowAccess2Lock) {
            return this.lock;
        }
        throw new IllegalStateException("Access to the lock disallowed.");
    }

    @Override
    public <V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception {
        //获得锁
        lock.lockInterruptibly();
        V result;
        try {
            //获得保护条件
            final Predicate guard = guardedAction.guard;
            //保护条件不成立  线程挂起  暂挂
            while (! guard.evaluate()) {
                log.info("alarm connecting alarm system,thread await");
                //条件不满足
                condition.await();
                //当线程从条件等待队列被唤醒后  获取锁成功
                //然后才去尝试判断条件是否满足
            }
            //条件满足  去执行目标动作
            System.out.println("alarm connected execute all");
            result = guardedAction.call();
            return result;
        } finally {
            //确保锁总会得到释放
            lock.unlock();
        }
    }

    public void signalAfter(Callable<Boolean> stateOperaion) throws Exception {
        lock.lockInterruptibly();
        try {
            if (stateOperaion.call()) {
                //条件满足唤醒
                System.out.println("alarm connected,signal thread ");
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void signal() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            condition.signal();

        } finally {
            lock.unlock();
        }
    }
}
