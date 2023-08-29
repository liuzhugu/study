package org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 等待 - 通知模式
 * */
public class GuardedQueue {
    private final Queue<Integer> sourceList;

    public GuardedQueue() {
        this.sourceList = new LinkedBlockingQueue<>();
    }

    public synchronized int get() {
        while (sourceList.isEmpty()) {
            try {
                System.out.println("before wait");
                //等待
                wait();
                System.out.println("after wait");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sourceList.peek();
    }

    public synchronized void put(Integer e) {

        sourceList.add(e);
        //使用notify的话   有线程可能永远得不到执行  所以尽量用notifyAll()
        System.out.println("begin notifyAll");
        //通知
        notifyAll();
    }
}
