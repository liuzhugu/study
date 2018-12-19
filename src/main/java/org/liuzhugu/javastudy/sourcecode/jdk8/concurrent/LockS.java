package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;


import java.util.concurrent.TimeUnit;


public interface LockS {

    void lock();

    void lockInterruptibly() throws InterruptedException;

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    void unlock();

    ConditionS newCondition();
}
