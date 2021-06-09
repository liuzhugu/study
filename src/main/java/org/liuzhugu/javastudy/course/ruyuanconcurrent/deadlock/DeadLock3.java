package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;


import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock3 {
    private static ReentrantLock lock1 = new ReentrantLock();
    private static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread a = new Thread(new Lock1());
        Thread b = new Thread(new Lock2());
        a.start();
        b.start();
    }

    static class Lock1 implements Runnable {
        @Override
        public void run() {
            //尝试获取锁  获取锁超时失败 释放自己占用的锁
            try {
                System.out.println("Lock1 running");
                while (true) {
                    if (lock1.tryLock(1, TimeUnit.MILLISECONDS)) {
                        System.out.println("Lock1 lock obj1");
                        if (lock2.tryLock(1, TimeUnit.MILLISECONDS)) {
                            System.out.println("Lock1 lock obj2");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    static class Lock2 implements Runnable {
        @Override
        public void run() {
            //尝试获取锁  获取锁失败 释放自己占用的锁
            try {
                System.out.println("Lock2 running");
                while (true) {
                    if (lock2.tryLock(1, TimeUnit.MILLISECONDS)) {
                        System.out.println("Lock2 lock obj1");
                        if (lock2.tryLock(1, TimeUnit.MILLISECONDS)) {
                            System.out.println("Lock2 lock obj2");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }
}
