package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;


import java.util.concurrent.Future;

public interface RunnableFuture_<V> extends Runnable_, Future_<V> {
    /**
     * Sets this Future to the result of its computation
     * unless it has been cancelled.
     */
    void run();
}
