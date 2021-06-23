package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;

public interface RunnableScheduledFuture_<V> extends RunnableFuture_<V>, ScheduledFuture_<V> {

    /**
     * Returns {@code true} if this task is periodic. A periodic task may
     * re-run according to some schedule. A non-periodic task can be
     * run only once.
     *
     * @return {@code true} if this task is periodic
     */
    boolean isPeriodic();
}