package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

public interface ThreadFactory_ {

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     *         create a thread is rejected
     */
    Thread_ newThread(Runnable_ r);
}
