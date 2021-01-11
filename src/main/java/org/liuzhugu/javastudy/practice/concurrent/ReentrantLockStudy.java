package org.liuzhugu.javastudy.practice.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockStudy {
    private static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        Condition condition = lock.newCondition();
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程1加锁成功");
                System.out.println("线程1执行await被挂起");
                condition.await();
                System.out.println("线程1被唤醒成功");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //得用个finally把解锁包起来
                lock.unlock();
                System.out.println("线程1释放锁成功");
            }
        }).start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程2加锁成功");
                condition.signal();
                System.out.println("线程2唤醒线程1");
            } finally {
                //得用个finally把解锁包起来
                lock.unlock();
                System.out.println("线程2释放锁成功");
            }
        }).start();
    }
}
