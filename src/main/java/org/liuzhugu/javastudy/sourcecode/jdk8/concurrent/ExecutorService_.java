package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public interface ExecutorService_ extends Executor_ {


    void shutdown();


    List<Runnable_> shutdownNow();


    boolean isShutdown();


    boolean isTerminated();


    boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;


    <T> Future_<T> submit(Callable<T> task);


    <T> Future_<T> submit(Runnable_ task, T result);

    Future_<?> submit(Runnable_ task);


    <T> List<Future_<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException;


    <T> List<Future_<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
            throws InterruptedException;


    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException;


    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}
