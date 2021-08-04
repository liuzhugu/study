package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.concurrent.*;

public class ExecutorCompletionService_<V> implements CompletionService_<V> {
    private final Executor_ Executor_;
    private final AbstractExecutorService_ aes;
    private final BlockingQueue<Future_<V>> completionQueue;

    /**
     * FutureTask_ extension to enqueue upon completion
     */
    private class QueueingFuture extends FutureTask_<Void> {
        QueueingFuture(RunnableFuture_<V> task) {
            super(task, null);
            this.task = task;
        }
        protected void done() { completionQueue.add(task); }
        private final Future_<V> task;
    }

    private RunnableFuture_<V> newTaskFor(Callable_<V> task) {
        if (aes == null)
            return new FutureTask_<>(task);
        else
            return aes.newTaskFor(task);
    }

    private RunnableFuture_<V> newTaskFor(Runnable_ task, V result) {
        if (aes == null)
            return new FutureTask_<V>(task, result);
        else
            return aes.newTaskFor(task, result);
    }

    /**
     * Creates an ExecutorCompletionService_ using the supplied
     * Executor_ for base task execution and a
     * {@link LinkedBlockingQueue} as a completion queue.
     *
     * @param Executor_ the Executor_ to use
     * @throws NullPointerException if Executor_ is {@code null}
     */
    public ExecutorCompletionService_(Executor_ Executor_) {
        if (Executor_ == null)
            throw new NullPointerException();
        this.Executor_ = Executor_;
        this.aes = (Executor_ instanceof AbstractExecutorService_) ?
                (AbstractExecutorService_) Executor_ : null;
        this.completionQueue = new LinkedBlockingQueue<Future_<V>>();
    }

    /**
     * Creates an ExecutorCompletionService_ using the supplied
     * Executor_ for base task execution and the supplied queue as its
     * completion queue.
     *
     * @param Executor_ the Executor_ to use
     * @param completionQueue the queue to use as the completion queue
     *        normally one dedicated for use by this service. This
     *        queue is treated as unbounded -- failed attempted
     *        {@code Queue.add} operations for completed tasks cause
     *        them not to be retrievable.
     * @throws NullPointerException if Executor_ or completionQueue are {@code null}
     */
    public ExecutorCompletionService_(Executor_ Executor_,
                                     BlockingQueue<Future_<V>> completionQueue) {
        if (Executor_ == null || completionQueue == null)
            throw new NullPointerException();
        this.Executor_ = Executor_;
        this.aes = (Executor_ instanceof AbstractExecutorService_) ?
                (AbstractExecutorService_) Executor_ : null;
        this.completionQueue = completionQueue;
    }

    public Future_<V> submit(Callable_<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture_<V> f = newTaskFor(task);
        Executor_.execute(new ExecutorCompletionService_.QueueingFuture(f));
        return f;
    }

    public Future_<V> submit(Runnable_ task, V result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture_<V> f = newTaskFor(task, result);
        Executor_.execute(new ExecutorCompletionService_.QueueingFuture(f));
        return f;
    }

    public Future_<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    public Future_<V> poll() {
        return completionQueue.poll();
    }

    public Future_<V> poll(long timeout, TimeUnit unit)
            throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

}