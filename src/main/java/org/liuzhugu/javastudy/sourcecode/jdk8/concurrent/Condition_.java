
package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public interface Condition_ {

    void await() throws InterruptedException;

    void awaitUninterruptibly();

    long awaitNanos(long nanosTimeout) throws InterruptedException;

    boolean await(long time, TimeUnit unit) throws InterruptedException;

    boolean awaitUntil(Date deadline) throws InterruptedException;

    void signal();

    void signalAll();
}
