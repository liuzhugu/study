package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


/**
 * 可重入锁,锁的持有人可以重复进入该锁
 * 总结:
 * 1.会有公平和非公平的实现,它们都会覆盖掉lock和tryAcquire来实现公平和非公平
 * 2.tryAcquire在AQS是直接抛异常,因此如果子类不覆盖该方法的话就无法正常工作
 * 3.默认为非公平,要想公平得在初始化时设为true
 * 4.公平直接acquire,如果等待队列为空才会尝试加锁,加锁失败进入等待队列
 * 5.非公平先直接CAS加锁,失败才执行acquire,并且队列不为空也直接加锁，加锁失败才进入等待队列
 * 6.在前一个线程释放锁之后,如果是非公平的话,新来的线程会比先来的线程多两次抢先加锁的机会,因此会出现先来加锁的线程被后面线程抢先的情况
 * */
public class ReentrantLockS implements LockS, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;

    /**
     * 可重入锁的实际实现是AQS的子类
     * */
    private final Sync sync;

    /**
     * 加锁,最终都是调用acquire,但如果是非公平的那么直接加锁,失败才调用acquire,
     * */
    public void lock() {
        sync.lock();
    }

    /**
     * 解锁,释放之后
     * */
    public void unlock() { sync.release(1); }

    /**
     * 通过CAS加锁,加锁失败放入AQS
     * AQS是个抽象队列同步器，保存被挂起的线程
     */
    abstract static class Sync extends AbstractQueuedSynchronizerS {
        private static final long serialVersionUID = -5179523762034025860L;

        abstract void lock();

        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            //未加锁的话
            if (c == 0) {
                // CAS来设置状态,设置成功的话认为加锁成功,设置锁的拥有者为当前线程
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            //已加锁,但当前线程为锁持有者
            else if (current == getExclusiveOwnerThread()) {
                //计数值+1
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            //尝试加锁失败
            return false;
        }

        /**
         * 解锁
         * */
        protected final boolean tryRelease(int releases) {
            //计数值-1
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            //计数值减完之后释放锁,修改锁持有者
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            //设置计数值
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }


        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

        private void readObject(java.io.ObjectInputStream s)
                throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    /**
     * 非公平
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * 非公平锁立马加锁,失败才acquire
         */
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * 公平
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }

        /**
         * 覆盖AQS的默认实现,其默认实现是直接抛异常,让子类知道需要覆盖掉该方法
         * 如果队列已经有等待线程那么加锁失败进入等待队列
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    /**
     * 默认非公平
     * */
    public ReentrantLockS() {
        sync = new NonfairSync();
    }

    /**
     * 初始化时设定是否公平
     * */
    public ReentrantLockS(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }











    /**
     * 非核心代码
     * */
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }
    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
    public ConditionS newCondition() {
        return sync.newCondition();
    }

    public int getHoldCount() {
        return sync.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    public boolean isLocked() {
        return sync.isLocked();
    }

    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    protected Thread getOwner() {
        return sync.getOwner();
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    public boolean hasWaiters(ConditionS condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizerS.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizerS.ConditionObject)condition);
    }

    public int getWaitQueueLength(ConditionS condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizerS.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizerS.ConditionObject)condition);
    }

    protected Collection<Thread> getWaitingThreads(ConditionS condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizerS.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizerS.ConditionObject)condition);
    }

    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                "[Unlocked]" :
                "[Locked by thread " + o.getName() + "]");
    }
}
