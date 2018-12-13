package org.liuzhugu.javastudy.sourcecode.java.concurrent;


import org.liuzhugu.javastudy.sourcecode.java.concurrent.ConditionStudy;

import java.util.concurrent.TimeUnit;


public interface LockStudy {

    void lock();

    void lockInterruptibly() throws InterruptedException;

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    void unlock();

    ConditionStudy newCondition();
}
