package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import sun.security.util.SecurityConstants;

import java.security.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mustWatch Executors
 * */
public class Executors_ {

  
    public static ExecutorService_ newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor_(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable_>());
    }

    /**
     * Creates a Thread_ pool that maintains enough threads to support
     * the given parallelism level, and may use multiple queues to
     * reduce contention. The parallelism level corresponds to the
     * maximum number of threads actively engaged in, or available to
     * engage in, task processing. The actual number of threads may
     * grow and shrink dynamically. A work-stealing pool makes no
     * guarantees about the order in which submitted tasks are
     * executed.
     *
     * @param parallelism the targeted parallelism level
     * @return the newly created Thread_ pool
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     * @since 1.8
     */
//    public static ExecutorService_ newWorkStealingPool(int parallelism) {
//        return new ForkJoinPool
//                (parallelism,
//                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
//                        null, true);
//    }

    /**
     * Creates a work-stealing Thread_ pool using all
     * {@link Runtime#availableProcessors available processors}
     * as its target parallelism level.
     * @return the newly created Thread_ pool
     * @see #newWorkStealingPool(int)
     * @since 1.8
     */
//    public static ExecutorService_ newWorkStealingPool() {
//        return new ForkJoinPool
//                (Runtime.getRuntime().availableProcessors(),
//                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
//                        null, true);
//    }

    /**
     * Creates a Thread_ pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.  At any point,
     * at most {@code nThreads} threads will be active processing
     * tasks.  If additional tasks are submitted when all threads are
     * active, they will wait in the queue until a Thread_ is
     * available.  If any Thread_ terminates due to a failure during
     * execution prior to shutdown, a new one will take its place if
     * needed to execute subsequent tasks.  The threads in the pool will
     * exist until it is explicitly {@link ExecutorService_#shutdown
     * shutdown}.
     *
     * @param nThreads the number of threads in the pool
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created Thread_ pool
     * @throws NullPointerException if threadFactory is null
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService_ newFixedThreadPool(int nThreads, ThreadFactory_ threadFactory) {
        return new ThreadPoolExecutor_(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable_>(),
                threadFactory);
    }

    /**
     * Creates an Executor that uses a single worker Thread_ operating
     * off an unbounded queue. (Note however that if this single
     * Thread_ terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newFixedThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorService_ newSingleThreadExecutor() {
        return new Executors_.FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor_(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable_>()));
    }

    /**
     * Creates an Executor that uses a single worker Thread_ operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new Thread_ when needed. Unlike the otherwise
     * equivalent {@code newFixedThreadPool(1, threadFactory)} the
     * returned executor is guaranteed not to be reconfigurable to use
     * additional threads.
     *
     * @param threadFactory the factory to use when creating new
     * threads
     *
     * @return the newly created single-threaded Executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService_ newSingleThreadExecutor(ThreadFactory_ threadFactory) {
        return new Executors_.FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor_(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable_>(),
                        threadFactory));
    }

    /**
     * Creates a Thread_ pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing Thread_ is available, a new
     * Thread_ will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor_} constructors.
     *
     * @return the newly created Thread_ pool
     */
    public static ExecutorService_ newCachedThreadPool() {
        return new ThreadPoolExecutor_(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable_>());
    }

    /**
     * Creates a Thread_ pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created Thread_ pool
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService_ newCachedThreadPool(ThreadFactory_ threadFactory) {
        return new ThreadPoolExecutor_(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable_>(),
                threadFactory);
    }

    /**
     * 线程池大小为1的可定时调度的任务线程池
     */
    public static ScheduledExecutorService_ newSingleThreadScheduledExecutor() {
        return new Executors_.DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor_(1));
    }

    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.  (Note
     * however that if this single Thread_ terminates due to a failure
     * during execution prior to shutdown, a new one will take its
     * place if needed to execute subsequent tasks.)  Tasks are
     * guaranteed to execute sequentially, and no more than one task
     * will be active at any given time. Unlike the otherwise
     * equivalent {@code newScheduledThreadPool(1, threadFactory)}
     * the returned executor is guaranteed not to be reconfigurable to
     * use additional threads.
     * @param threadFactory the factory to use when creating new
     * threads
     * @return a newly created scheduled executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ScheduledExecutorService_ newSingleThreadScheduledExecutor(ThreadFactory_ threadFactory) {
        return new Executors_.DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor_(1, threadFactory));
    }

    /**
     * Creates a Thread_ pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     * @param corePoolSize the number of threads to keep in the pool,
     * even if they are idle
     * @return a newly created scheduled Thread_ pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public static ScheduledExecutorService_ newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor_(corePoolSize);
    }

    /**
     * Creates a Thread_ pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     * @param corePoolSize the number of threads to keep in the pool,
     * even if they are idle
     * @param threadFactory the factory to use when the executor
     * creates a new Thread_
     * @return a newly created scheduled Thread_ pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @throws NullPointerException if threadFactory is null
     */
    public static ScheduledExecutorService_ newScheduledThreadPool(
            int corePoolSize, ThreadFactory_ threadFactory) {
        return new ScheduledThreadPoolExecutor_(corePoolSize, threadFactory);
    }

    /**
     * Returns an object that delegates all defined {@link
     * ExecutorService_} methods to the given executor, but not any
     * other methods that might otherwise be accessible using
     * casts. This provides a way to safely "freeze" configuration and
     * disallow tuning of a given concrete implementation.
     * @param executor the underlying implementation
     * @return an {@code ExecutorService_} instance
     * @throws NullPointerException if executor null
     */
    public static ExecutorService_ unconfigurableExecutorService(ExecutorService_ executor) {
        if (executor == null)
            throw new NullPointerException();
        return new Executors_.DelegatedExecutorService(executor);
    }

    /**
     * Returns an object that delegates all defined {@link
     * ScheduledExecutorService_} methods to the given executor, but
     * not any other methods that might otherwise be accessible using
     * casts. This provides a way to safely "freeze" configuration and
     * disallow tuning of a given concrete implementation.
     * @param executor the underlying implementation
     * @return a {@code ScheduledExecutorService_} instance
     * @throws NullPointerException if executor null
     */
    public static ScheduledExecutorService_ unconfigurableScheduledExecutorService(ScheduledExecutorService_ executor) {
        if (executor == null)
            throw new NullPointerException();
        return new Executors_.DelegatedScheduledExecutorService(executor);
    }

    /**
     * Returns a default Thread_ factory used to create new threads.
     * This factory creates all new threads used by an Executor in the
     * same {@link ThreadGroup}. If there is a {@link
     * java.lang.SecurityManager}, it uses the group of {@link
     * System#getSecurityManager}, else the group of the Thread_
     * invoking this {@code defaultThreadFactory} method. Each new
     * Thread_ is created as a non-daemon Thread_ with priority set to
     * the smaller of {@code Thread_.NORM_PRIORITY} and the maximum
     * priority permitted in the Thread_ group.  New threads have names
     * accessible via {@link Thread_#getName} of
     * <em>pool-N-Thread_-M</em>, where <em>N</em> is the sequence
     * number of this factory, and <em>M</em> is the sequence number
     * of the Thread_ created by this factory.
     * @return a Thread_ factory
     */
    public static ThreadFactory_ defaultThreadFactory() {
        return new Executors_.DefaultThreadFactory();
    }

    public static ThreadFactory_ privilegedThreadFactory() {
        return new Executors_.PrivilegedThreadFactory();
    }

    /**
     * Returns a {@link Callable_} object that, when
     * called, runs the given task and returns the given result.  This
     * can be useful when applying methods requiring a
     * {@code Callable_} to an otherwise resultless action.
     * @param task the task to run
     * @param result the result to return
     * @param <T> the type of the result
     * @return a Callable_ object
     * @throws NullPointerException if task null
     */
    public static <T> Callable_<T> Callable_(Runnable_ task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new Executors_.RunnableAdapter<T>(task, result);
    }

    /**
     * Returns a {@link Callable_} object that, when
     * called, runs the given task and returns {@code null}.
     * @param task the task to run
     * @return a Callable_ object
     * @throws NullPointerException if task null
     */
    public static Callable_<Object> Callable_(Runnable_ task) {
        if (task == null)
            throw new NullPointerException();
        return new Executors_.RunnableAdapter<Object>(task, null);
    }

    /**
     * Returns a {@link Callable_} object that, when
     * called, runs the given privileged action and returns its result.
     * @param action the privileged action to run
     * @return a Callable_ object
     * @throws NullPointerException if action null
     */
    public static Callable_<Object> Callable_(final PrivilegedAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable_<Object>() {
            public Object call() { return action.run(); }};
    }

    /**
     * Returns a {@link Callable_} object that, when
     * called, runs the given privileged exception action and returns
     * its result.
     * @param action the privileged exception action to run
     * @return a Callable_ object
     * @throws NullPointerException if action null
     */
    public static Callable_<Object> Callable_(final PrivilegedExceptionAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable_<Object>() {
            public Object call() throws Exception { return action.run(); }};
    }

    /**
     * Returns a {@link Callable_} object that will, when called,
     * execute the given {@code Callable_} under the current access
     * control context. This method should normally be invoked within
     * an {@link AccessController#doPrivileged AccessController.doPrivileged}
     * action to create callables that will, if possible, execute
     * under the selected permission settings holding within that
     * action; or if not possible, throw an associated {@link
     * AccessControlException}.
     * @param Callable_ the underlying task
     * @param <T> the type of the Callable_'s result
     * @return a Callable_ object
     * @throws NullPointerException if Callable_ null
     */
    public static <T> Callable_<T> privilegedCallable(Callable_<T> Callable_) {
        if (Callable_ == null)
            throw new NullPointerException();
        return new Executors_.PrivilegedCallable<T>(Callable_);
    }

    /**
     * Returns a {@link Callable_} object that will, when called,
     * execute the given {@code Callable_} under the current access
     * control context, with the current context class loader as the
     * context class loader. This method should normally be invoked
     * within an
     * {@link AccessController#doPrivileged AccessController.doPrivileged}
     * action to create callables that will, if possible, execute
     * under the selected permission settings holding within that
     * action; or if not possible, throw an associated {@link
     * AccessControlException}.
     *
     * @param Callable_ the underlying task
     * @param <T> the type of the Callable_'s result
     * @return a Callable_ object
     * @throws NullPointerException if Callable_ null
     * @throws AccessControlException if the current access control
     * context does not have permission to both set and get context
     * class loader
     */
    public static <T> Callable_<T> privilegedCallableUsingCurrentClassLoader(Callable_<T> Callable_) {
        if (Callable_ == null)
            throw new NullPointerException();
        return new Executors_.PrivilegedCallableUsingCurrentClassLoader<T>(Callable_);
    }

    // Non-public classes supporting the public methods

    /**
     * A Callable_ that runs given task and returns given result
     */
    static final class RunnableAdapter<T> implements Callable_<T> {
        final Runnable_ task;
        final T result;
        RunnableAdapter(Runnable_ task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }

    /**
     * A Callable_ that runs under established access control settings
     */
    static final class PrivilegedCallable<T> implements Callable_<T> {
        private final Callable_<T> task;
        private final AccessControlContext acc;

        PrivilegedCallable(Callable_<T> task) {
            this.task = task;
            this.acc = AccessController.getContext();
        }

        public T call() throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run() throws Exception {
                                return task.call();
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * A Callable_ that runs under established access control settings and
     * current ClassLoader
     */
    static final class PrivilegedCallableUsingCurrentClassLoader<T> implements Callable_<T> {
        private final Callable_<T> task;
        private final AccessControlContext acc;
        private final ClassLoader ccl;

        PrivilegedCallableUsingCurrentClassLoader(Callable_<T> task) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                // Calls to getContextClassLoader from this class
                // never trigger a security check, but we check
                // whether our callers have this permission anyways.
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

                // Whether setContextClassLoader turns out to be necessary
                // or not, we fail fast if permission is not available.
                sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            }
            this.task = task;
            this.acc = AccessController.getContext();
            this.ccl = Thread_.currentThread().getContextClassLoader();
        }

        public T call() throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run() throws Exception {
                                Thread_ t = Thread_.currentThread();
                                ClassLoader cl = t.getContextClassLoader();
                                if (ccl == cl) {
                                    return task.call();
                                } else {
                                    t.setContextClassLoader(ccl);
                                    try {
                                        return task.call();
                                    } finally {
                                        t.setContextClassLoader(cl);
                                    }
                                }
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * The default Thread_ factory
     */
    static class DefaultThreadFactory implements ThreadFactory_ {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup_ group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = Thread_.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread_-";
        }

        public Thread_ newThread(Runnable_ r) {
            Thread_ t = new Thread_(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread_.NORM_PRIORITY)
                t.setPriority(Thread_.NORM_PRIORITY);
            return t;
        }
    }

    /**
     * Thread_ factory capturing access control context and class loader
     */
    static class PrivilegedThreadFactory extends Executors_.DefaultThreadFactory {
        private final AccessControlContext acc;
        private final ClassLoader ccl;

        PrivilegedThreadFactory() {
            super();
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                // Calls to getContextClassLoader from this class
                // never trigger a security check, but we check
                // whether our callers have this permission anyways.
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

                // Fail fast
                sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            }
            this.acc = AccessController.getContext();
            this.ccl = Thread_.currentThread().getContextClassLoader();
        }

        public Thread_ newThread(final Runnable_ r) {
            return super.newThread(new Runnable_() {
                public void run() {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                            Thread_.currentThread().setContextClassLoader(ccl);
                            r.run();
                            return null;
                        }
                    }, acc);
                }
            });
        }
    }

    /**
     * A wrapper class that exposes only the ExecutorService_ methods
     * of an ExecutorService_ implementation.
     */
    static class DelegatedExecutorService extends AbstractExecutorService_ {
        private final ExecutorService_ e;
        DelegatedExecutorService(ExecutorService_ executor) { e = executor; }
        public void execute(Runnable_ command) { e.execute(command); }
        public void shutdown() { e.shutdown(); }
        public List<Runnable_> shutdownNow() { return e.shutdownNow(); }
        public boolean isShutdown() { return e.isShutdown(); }
        public boolean isTerminated() { return e.isTerminated(); }
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }
        public Future_<?> submit(Runnable_ task) {
            return e.submit(task);
        }
        public <T> Future_<T> submit(Callable_<T> task) {
            return e.submit(task);
        }
        public <T> Future_<T> submit(Runnable_ task, T result) {
            return e.submit(task, result);
        }
        public <T> List<Future_<T>> invokeAll(Collection<? extends Callable_<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }
        public <T> List<Future_<T>> invokeAll(Collection<? extends Callable_<T>> tasks,
                                             long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }
        public <T> T invokeAny(Collection<? extends Callable_<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }
        public <T> T invokeAny(Collection<? extends Callable_<T>> tasks,
                               long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

    static class FinalizableDelegatedExecutorService
            extends Executors_.DelegatedExecutorService {
        FinalizableDelegatedExecutorService(ExecutorService_ executor) {
            super(executor);
        }
        protected void finalize() {
            super.shutdown();
        }
    }

    /**
     * A wrapper class that exposes only the ScheduledExecutorService_
     * methods of a ScheduledExecutorService_ implementation.
     */
    static class DelegatedScheduledExecutorService
            extends Executors_.DelegatedExecutorService
            implements ScheduledExecutorService_ {
        private final ScheduledExecutorService_ e;
        DelegatedScheduledExecutorService(ScheduledExecutorService_ executor) {
            super(executor);
            e = executor;
        }
        public ScheduledFuture_<?> schedule(Runnable_ command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }
        public <V> ScheduledFuture_<V> schedule(Callable_<V> Callable_, long delay, TimeUnit unit) {
            return e.schedule(Callable_, delay, unit);
        }
        public ScheduledFuture_<?> scheduleAtFixedRate(Runnable_ command, long initialDelay, long period, TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
        public ScheduledFuture_<?> scheduleWithFixedDelay(Runnable_ command, long initialDelay, long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }

    /** Cannot instantiate. */
    private Executors_() {}
}
