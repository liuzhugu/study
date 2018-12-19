package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;


import java.util.concurrent.TimeUnit;


public interface Lock_ {

    void lock();

    void lockInterruptibly() throws InterruptedException;

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    void unlock();

    Condition_ newCondition();
}
