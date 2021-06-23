package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public interface CompletionService_<V> {
    /**
     * Submits a value-returning task for execution and returns a Future_
     * representing the pending results of the task.  Upon completion,
     * this task may be taken or polled.
     *
     * @param task the task to submit
     * @return a Future_ representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    Future_<V> submit(Callable<V> task);

    /**
     * Submits a Runnable_ task for execution and returns a Future_
     * representing that task.  Upon completion, this task may be
     * taken or polled.
     *
     * @param task the task to submit
     * @param result the result to return upon successful completion
     * @return a Future_ representing pending completion of the task,
     *         and whose {@code get()} method will return the given
     *         result value upon completion
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    Future_<V> submit(Runnable_ task, V result);

    /**
     * Retrieves and removes the Future_ representing the next
     * completed task, waiting if none are yet present.
     *
     * @return the Future_ representing the next completed task
     * @throws InterruptedException if interrupted while waiting
     */
    Future_<V> take() throws InterruptedException;

    /**
     * Retrieves and removes the Future_ representing the next
     * completed task, or {@code null} if none are present.
     *
     * @return the Future_ representing the next completed task, or
     *         {@code null} if none are present
     */
    Future_<V> poll();

    /**
     * Retrieves and removes the Future_ representing the next
     * completed task, waiting if necessary up to the specified wait
     * time if none are yet present.
     *
     * @param timeout how long to wait before giving up, in units of
     *        {@code unit}
     * @param unit a {@code TimeUnit} determining how to interpret the
     *        {@code timeout} parameter
     * @return the Future_ representing the next completed task or
     *         {@code null} if the specified waiting time elapses
     *         before one is present
     * @throws InterruptedException if interrupted while waiting
     */
    Future_<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
}
