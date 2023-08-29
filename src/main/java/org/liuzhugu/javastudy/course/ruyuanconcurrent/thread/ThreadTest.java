package org.liuzhugu.javastudy.course.ruyuanconcurrent.thread;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class ThreadTest extends Thread{
    private String name;

    public ThreadTest(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 1;i < 11;i++) {
            System.out.println(Thread.currentThread().getName() + " thread " + i);
        }
    }

    public static void main(String[] args) {
        ThreadTest t1 = new ThreadTest("thread1");
        ThreadTest t2 = new ThreadTest("thread2");
        ThreadTest t3 = new ThreadTest("thread3");
        t1.start();
        t2.start();
        t3.start();
    }
}
