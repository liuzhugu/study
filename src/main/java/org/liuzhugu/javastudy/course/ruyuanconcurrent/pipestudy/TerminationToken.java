package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {
    //修改后要让其他线程及时见到
    protected volatile boolean toShutdown;

    //未完成的任务数
    public final AtomicInteger reservations = new AtomicInteger(0);

    /**
     * 挂在同一个token下的所有线程  当其中一个线程终止时  其余线程也该终止
     * */
    private final Queue<WeakReference<Termination>> coordinatedThreads;

    public TerminationToken() {
        this.coordinatedThreads = new ConcurrentLinkedQueue();
    }

    public boolean isToShutdown() {
        return toShutdown;
    }

    public void setToShutdown() {
        this.toShutdown = true;
    }

    public void register(Termination thread) {
        coordinatedThreads.add(new WeakReference<>(thread));
    }

    public void notifyThreadToken(Termination thread) {
        WeakReference<Termination> wrThread;
        Termination otherThread;
        while ((wrThread = coordinatedThreads.poll()) != null) {
            otherThread = wrThread.get();
            //终止其他线程
            if (null != otherThread && otherThread != thread) {
                otherThread.terminate();
            }
        }
    }
}
