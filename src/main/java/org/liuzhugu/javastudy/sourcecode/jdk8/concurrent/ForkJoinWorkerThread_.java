package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.security.AccessControlContext;
import java.security.ProtectionDomain;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinWorkerThread_ extends Thread_ {
    /*
     * ForkJoinWorkerThread_s are managed by ForkJoinPools and perform
     * ForkJoinTasks. For explanation, see the internal documentation
     * of class ForkJoinPool_.
     *
     * This class just maintains links to its pool and WorkQueue.  The
     * pool field is set immediately upon construction, but the
     * workQueue field is not set until a call to registerWorker
     * completes. This leads to a visibility race, that is tolerated
     * by requiring that the workQueue field is only accessed by the
     * owning thread.
     *
     * Support for (non-public) subclass InnocuousForkJoinWorkerThread_
     * requires that we break quite a lot of encapsulation (via Unsafe)
     * both here and in the subclass to access and set Thread fields.
     */

    final ForkJoinPool_ pool;                // the pool this thread works in
    final ForkJoinPool_.WorkQueue workQueue; // work-stealing mechanics

    /**
     * Creates a ForkJoinWorkerThread_ operating in the given pool.
     *
     * @param pool the pool this thread works in
     * @throws NullPointerException if pool is null
     */
    protected ForkJoinWorkerThread_(ForkJoinPool_ pool) {
        // Use a placeholder until a useful name can be set in registerWorker
        super("aForkJoinWorkerThread_");
        this.pool = pool;
        this.workQueue = pool.registerWorker(this);
    }

    /**
     * Version for InnocuousForkJoinWorkerThread_
     */
    ForkJoinWorkerThread_(ForkJoinPool_ pool, ThreadGroup_ ThreadGroup_,
                         AccessControlContext acc) {
        super(ThreadGroup_, null, "aForkJoinWorkerThread_");
        U.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, acc);
        eraseThreadLocals(); // clear before registering
        this.pool = pool;
        this.workQueue = pool.registerWorker(this);
    }

    /**
     * Returns the pool hosting this thread.
     *
     * @return the pool
     */
    public ForkJoinPool_ getPool() {
        return pool;
    }

    /**
     * Returns the unique index number of this thread in its pool.
     * The returned value ranges from zero to the maximum number of
     * threads (minus one) that may exist in the pool, and does not
     * change during the lifetime of the thread.  This method may be
     * useful for applications that track status or collect results
     * per-worker-thread rather than per-task.
     *
     * @return the index number
     */
    public int getPoolIndex() {
        return workQueue.getPoolIndex();
    }

    /**
     * Initializes internal state after construction but before
     * processing any tasks. If you override this method, you must
     * invoke {@code super.onStart()} at the beginning of the method.
     * Initialization requires care: Most fields must have legal
     * default values, to ensure that attempted accesses from other
     * threads work correctly even before this thread starts
     * processing tasks.
     */
    protected void onStart() {
    }

    /**
     * Performs cleanup associated with termination of this worker
     * thread.  If you override this method, you must invoke
     * {@code super.onTermination} at the end of the overridden method.
     *
     * @param exception the exception causing this thread to abort due
     * to an unrecoverable error, or {@code null} if completed normally
     */
    protected void onTermination(Throwable exception) {
    }

    /**
     * This method is required to be public, but should never be
     * called explicitly. It performs the main run loop to execute
     * {@link ForkJoinTask}s.
     */
    public void run() {
        if (workQueue.array == null) { // only run once
            Throwable exception = null;
            try {
                onStart();
                pool.runWorker(workQueue);
            } catch (Throwable ex) {
                exception = ex;
            } finally {
                try {
                    onTermination(exception);
                } catch (Throwable ex) {
                    if (exception == null)
                        exception = ex;
                } finally {
                    pool.deregisterWorker(this, exception);
                }
            }
        }
    }

    /**
     * Erases ThreadLocals by nulling out Thread maps.
     */
    final void eraseThreadLocals() {
        U.putObject(this, THREADLOCALS, null);
        U.putObject(this, INHERITABLETHREADLOCALS, null);
    }

    /**
     * Non-public hook method for InnocuousForkJoinWorkerThread_
     */
    void afterTopLevelExec() {
    }

    // Set up to allow setting thread fields in constructor
    private static final sun.misc.Unsafe U;
    private static final long THREADLOCALS;
    private static final long INHERITABLETHREADLOCALS;
    private static final long INHERITEDACCESSCONTROLCONTEXT;
    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> tk = Thread.class;
            THREADLOCALS = U.objectFieldOffset
                    (tk.getDeclaredField("threadLocals"));
            INHERITABLETHREADLOCALS = U.objectFieldOffset
                    (tk.getDeclaredField("inheritableThreadLocals"));
            INHERITEDACCESSCONTROLCONTEXT = U.objectFieldOffset
                    (tk.getDeclaredField("inheritedAccessControlContext"));

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * A worker thread that has no permissions, is not a member of any
     * user-defined ThreadGroup_, and erases all ThreadLocals after
     * running each top-level task.
     */
    static final class InnocuousForkJoinWorkerThread_ extends ForkJoinWorkerThread_ {
        /** The ThreadGroup_ for all InnocuousForkJoinWorkerThread_s */
        private static final ThreadGroup_ innocuousThreadGroup =
                createThreadGroup();

        /** An AccessControlContext supporting no privileges */
        private static final AccessControlContext INNOCUOUS_ACC =
                new AccessControlContext(
                        new ProtectionDomain[] {
                                new ProtectionDomain(null, null)
                        });

        InnocuousForkJoinWorkerThread_(ForkJoinPool_ pool) {
            super(pool, innocuousThreadGroup, INNOCUOUS_ACC);
        }

        @Override // to erase ThreadLocals
        void afterTopLevelExec() {
            eraseThreadLocals();
        }

        @Override // to always report system loader
        public ClassLoader getContextClassLoader() {
            return ClassLoader.getSystemClassLoader();
        }

        @Override // to silently fail
        public void setUncaughtExceptionHandler(UncaughtExceptionHandler x) { }

        @Override // paranoically
        public void setContextClassLoader(ClassLoader cl) {
            throw new SecurityException("setContextClassLoader");
        }

        /**
         * Returns a new group with the system ThreadGroup_ (the
         * topmost, parent-less group) as parent.  Uses Unsafe to
         * traverse Thread.group and ThreadGroup_.parent fields.
         */
        private static ThreadGroup_ createThreadGroup() {
            try {
                sun.misc.Unsafe u = sun.misc.Unsafe.getUnsafe();
                Class<?> tk = Thread.class;
                Class<?> gk = ThreadGroup_.class;
                long tg = u.objectFieldOffset(tk.getDeclaredField("group"));
                long gp = u.objectFieldOffset(gk.getDeclaredField("parent"));
                ThreadGroup_ group = (ThreadGroup_)
                        u.getObject(Thread.currentThread(), tg);
                while (group != null) {
                    ThreadGroup_ parent = (ThreadGroup_)u.getObject(group, gp);
                    if (parent == null)
                        return new ThreadGroup_(group,
                                "InnocuousForkJoinWorkerThread_Group");
                    group = parent;
                }
            } catch (Exception e) {
                throw new Error(e);
            }
            // fall through if null as cannot-happen safeguard
            throw new Error("Cannot create ThreadGroup_");
        }
    }

}