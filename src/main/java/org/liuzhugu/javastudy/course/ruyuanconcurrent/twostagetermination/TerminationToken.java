package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;



import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:线程停止标志
 **/
public class TerminationToken {

    /**
     * 通过volatile来修饰，无锁的请求下当数据修改后其他线程可以读取到，是否停止标志
     */
    protected volatile boolean toShutdowm = false;

    /**
     * 未执行的任务的数量
     * */
    public final AtomicInteger reservations = new AtomicInteger(0);

    /**
     * 当多个线程共享一个TerminationToken实例时  通过队列的方式来记录所有的停止线程
     * 减少锁的方式来实现
     * */
    private final Queue<WeakReference<Termination>> coordinatedThreads;

    public TerminationToken() {
        this.coordinatedThreads = new ConcurrentLinkedQueue<>();
    }

    /**
     * 是否终止
     * */
    public boolean isToShutdowm() {
        return toShutdowm;
    }

    public void setToShutdowm(boolean toShutdowm) {
        this.toShutdowm = toShutdowm;
    }

    /**
     * 注册一个线程到TerminationToken上
     * */
    public void register(Termination thread) {
        coordinatedThreads.add(new WeakReference<>(thread));
    }

    /**
     * 通知TerminationToken中所有实例  有一个线程停止了  通知其他线程也停止
     * */
    public void notifyThreadTermination(Termination thread) {
        WeakReference<Termination> wrThread;
        Termination otherThread;

        while ((wrThread = coordinatedThreads.poll()) != null) {
            otherThread = wrThread.get();
            if (otherThread != null && otherThread != thread) {
                otherThread.terminate();
            }
        }
    }
}
