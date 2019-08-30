package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import org.liuzhugu.javastudy.sourcecode.jdk8.container.map.HashMap_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.map.Map_;
import sun.security.util.SecurityConstants;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
public class Thread_ implements Runnable_ {

    /**
     * 核心API
     * */

    /**
     * 当前线程休眠
     * */
    public static void sleep(long millis, int nanos) throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        sleep(millis);
    }
    /**
     * 告知调度器,当前线程愿让出CPU,但调度器不一定理会
     * */
    public static native void yield();
    /**
     * 执行这个方法才会新开一个线程来执行thread,直接调run方法是在原来线程执行
     * */
    public synchronized void start() {
        /**
         * 线程状态得是NEW
         * */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* 将该线程加入待执行线程组 */
        group.add(this);

        boolean started = false;
        try {
            //执行本地方法start0
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }
    /**
     * 要么执行传进来的runnable实现类,要么等待子类覆盖该方法,否则什么都不做
     */
    public void run() {
        if (target != null) {
            target.run();
        }
    }
    /**
     * 只是发送中断线程信号,若不处理,那么相当于没发
     * 然后目标线程可以在自己期望的检查点才来检查该状态
     */
    public void interrupt() {
        if (this != Thread_.currentThread())
            checkAccess();

        synchronized (blockerLock) {
            Interruptible_ b = blocker;
            if (b != null) {
                interrupt0();           // Just to set the interrupt flag
                b.interrupt(this);
                return;
            }
        }
        interrupt0();
    }
    /**
     * 等待线程执行完
     * */
    public final synchronized void join(long millis, int nanos) throws InterruptedException {

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        join(millis);
    }

    public enum State {
        /**
         * 新创建,未调用start方法的线程
         */
        NEW,

        /**
         * 正在执行
         */
        RUNNABLE,

        /**
         * 等待锁而阻塞
         */
        BLOCKED,

        /**
         * 调用wait和join而挂起时的状态
         */
        WAITING,

        /**
         * 调用wait  join  sleep而挂起的状态
         */
        TIMED_WAITING,

        /**
         * 线程执行完
         */
        TERMINATED;
    }




    /* Make sure registerNatives is the first thing <clinit> does. */
    private static native void registerNatives();
    static {
        registerNatives();
    }
    private native void start0();

    private volatile char  name[];
    private int            priority;
    private Thread_         threadQ;
    private long           eetop;

    /* Whether or not to single_step this thread. */
    private boolean     single_step;

    /*只剩守护线程的时候程序会退出*/
    private boolean     daemon = false;

    /* JVM state */
    private boolean     stillborn = false;

    /* What will be run. */
    private Runnable_ target;

    /* The group of this thread */
    private ThreadGroup_ group;

    /* The context ClassLoader for this thread */
    private ClassLoader contextClassLoader;

    /* The inherited AccessControlContext of this thread */
    private AccessControlContext inheritedAccessControlContext;

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal_.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal_.ThreadLocalMap inheritableThreadLocals = null;

    /*
     * The requested stack size for this thread, or 0 if the creator did
     * not specify a stack size.  It is up to the VM to do whatever it
     * likes with this number; some VMs will ignore it.
     */
    private long stackSize;

    /*
     * JVM-private state that persists after native thread termination.
     */
    private long nativeParkEventPointer;

    /*
     * Thread_ ID
     */
    private long tid;

    /* For generating thread ID */
    private static long threadSeqNumber;

    /* Java thread status for tools,
     * initialized to indicate thread 'not yet started'
     */

    private volatile int threadStatus = 0;


    private static synchronized long nextThreadID() {
        return ++threadSeqNumber;
    }


    volatile Object parkBlocker;

    private volatile Interruptible_ blocker;
    private final Object blockerLock = new Object();

    /* Set the blocker field; invoked via sun.misc.SharedSecrets from java.nio code
     */
    void blockedOn(Interruptible_ b) {
        synchronized (blockerLock) {
            blocker = b;
        }
    }

    /**
     * The minimum priority that a thread can have.
     */
    public final static int MIN_PRIORITY = 1;

    /**
     * The default priority that is assigned to a thread.
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a thread can have.
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing thread object.
     *
     * @return  the currently executing thread.
     */
    public static native Thread_ currentThread();

    //

    public static native void sleep(long millis) throws InterruptedException;


    /**
     * Initializes a Thread_ with the current AccessControlContext.
     * @see #init(ThreadGroup_,Runnable_,String,long,AccessControlContext)
     */
    private void init(ThreadGroup_ g, Runnable_ target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null);
    }


    private void init(ThreadGroup_ g, Runnable_ target, String name,
                      long stackSize, AccessControlContext acc) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name.toCharArray();

        Thread_ parent = currentThread();
        SecurityManager_ security = System_.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
//        if (security != null) {
//            if (isCCLOverridden(getClass())) {
//                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
//            }
//        }

        g.addUnstarted();

        this.group = g;
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
//        if (security == null || isCCLOverridden(parent.getClass()))
//            this.contextClassLoader = parent.getContextClassLoader();
//        else
//            this.contextClassLoader = parent.contextClassLoader;
        this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                    ThreadLocal_.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Thread_() {
        init(null, null, "Thread_-" + nextThreadNum(), 0);
    }

    public Thread_(Runnable_ target) {
        init(null, target, "Thread_-" + nextThreadNum(), 0);
    }

    /**
     * Creates a new Thread_ that inherits the given AccessControlContext.
     * This is not a public constructor.
     */
    Thread_(Runnable_ target, AccessControlContext acc) {
        init(null, target, "Thread_-" + nextThreadNum(), 0, acc);
    }
    public Thread_(ThreadGroup_ group, Runnable_ target) {
        init(group, target, "Thread_-" + nextThreadNum(), 0);
    }
    public Thread_(String name) {
        init(null, null, name, 0);
    }
    public Thread_(ThreadGroup_ group, String name) {
        init(group, null, name, 0);
    }
    public Thread_(Runnable_ target, String name) {
        init(null, target, name, 0);
    }
    public Thread_(ThreadGroup_ group, Runnable_ target, String name) {
        init(group, target, name, 0);
    }
    public Thread_(ThreadGroup_ group, Runnable_ target, String name,
                  long stackSize) {
        init(group, target, name, stackSize);
    }




    /**
     * This method is called by the system to give a Thread_
     * a chance to clean up before it actually exits.
     */
    private void exit() {
        if (group != null) {
            group.threadTerminated(this);
            group = null;
        }
        /* Aggressively null out all reference fields: see bug 4006245 */
        target = null;
        /* Speed the release of some of these resources */
        threadLocals = null;
        inheritableThreadLocals = null;
        inheritedAccessControlContext = null;
        blocker = null;
        uncaughtExceptionHandler = null;
    }

    @Deprecated
    public final void stop() {
        SecurityManager_ security = System_.getSecurityManager();
        if (security != null) {
            checkAccess();
            if (this != Thread_.currentThread()) {
                security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
            }
        }
        // A zero status value corresponds to "NEW", it can't change to
        // not-NEW because we hold the lock.
        if (threadStatus != 0) {
            resume(); // Wake up thread if it was suspended; no-op otherwise
        }

        // The VM can handle all thread states
        stop0(new ThreadDeath());
    }

    @Deprecated
    public final synchronized void stop(Throwable obj) {
        throw new UnsupportedOperationException();
    }




    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }

    public boolean isInterrupted() {
        return isInterrupted(false);
    }

    /**
     * Tests if some Thread_ has been interrupted.  The interrupted state
     * is reset or not based on the value of ClearInterrupted that is
     * passed.
     */
    private native boolean isInterrupted(boolean ClearInterrupted);

    @Deprecated
    public void destroy() {
        throw new NoSuchMethodError();
    }

    /**
     * Tests if this thread is alive. A thread is alive if it has
     * been started and has not yet died.
     *
     * @return  <code>true</code> if this thread is alive;
     *          <code>false</code> otherwise.
     */
    public final native boolean isAlive();

    @Deprecated
    public final void suspend() {
        checkAccess();
        suspend0();
    }

    @Deprecated
    public final void resume() {
        checkAccess();
        resume0();
    }

    public final void setPriority(int newPriority) {
        ThreadGroup_ g;
        checkAccess();
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        if((g = getThreadGroup()) != null) {
            if (newPriority > g.getMaxPriority()) {
                newPriority = g.getMaxPriority();
            }
            setPriority0(priority = newPriority);
        }
    }


    public final int getPriority() {
        return priority;
    }

    public final synchronized void setName(String name) {
        checkAccess();
        this.name = name.toCharArray();
        if (threadStatus != 0) {
            setNativeName(name);
        }
    }

    public final String getName() {
        return new String(name);
    }

    public final ThreadGroup_ getThreadGroup() {
        return group;
    }

    public static int activeCount() {
        return currentThread().getThreadGroup().activeCount();
    }

    public static int enumerate(Thread_ tarray[]) {
        return currentThread().getThreadGroup().enumerate(tarray);
    }


    @Deprecated
    public native int countStackFrames();

    public final synchronized void join(long millis)
            throws InterruptedException {
        long base = System_.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System_.currentTimeMillis() - base;
            }
        }
    }


    public final void join() throws InterruptedException {
        join(0);
    }

    /**
     * Prints a stack trace of the current thread to the standard error stream.
     * This method is used only for debugging.
     *
     * @see     Throwable#printStackTrace()
     */
    public static void dumpStack() {
        new Exception("Stack trace").printStackTrace();
    }

    /**
     * Marks this thread as either a {@linkplain #isDaemon daemon} thread
     * or a user thread. The Java Virtual Machine exits when the only
     * threads running are all daemon threads.
     *
     * <p> This method must be invoked before the thread is started.
     *
     * @param  on
     *         if {@code true}, marks this thread as a daemon thread
     *
     * @throws  IllegalThreadStateException
     *          if this thread is {@linkplain #isAlive alive}
     *
     * @throws  SecurityException
     *          if {@link #checkAccess} determines that the current
     *          thread cannot modify this thread
     */
    public final void setDaemon(boolean on) {
        checkAccess();
        if (isAlive()) {
            throw new IllegalThreadStateException();
        }
        daemon = on;
    }

    /**
     * Tests if this thread is a daemon thread.
     *
     * @return  <code>true</code> if this thread is a daemon thread;
     *          <code>false</code> otherwise.
     * @see     #setDaemon(boolean)
     */
    public final boolean isDaemon() {
        return daemon;
    }

    /**
     * Determines if the currently running thread has permission to
     * modify this thread.
     * <p>
     * If there is a security manager, its <code>checkAccess</code> method
     * is called with this thread as its argument. This may result in
     * throwing a <code>SecurityException</code>.
     *
     * @exception  SecurityException  if the current thread is not allowed to
     *               access this thread.
     * @see        SecurityManager_#checkAccess(Thread_)
     */
    public final void checkAccess() {
        SecurityManager_ security = System_.getSecurityManager();
        if (security != null) {
            security.checkAccess(this);
        }
    }

    /**
     * Returns a string representation of this thread, including the
     * thread's name, priority, and thread group.
     *
     * @return  a string representation of this thread.
     */
    public String toString() {
        ThreadGroup_ group = getThreadGroup();
        if (group != null) {
            return "Thread_[" + getName() + "," + getPriority() + "," +
                    group.getName() + "]";
        } else {
            return "Thread_[" + getName() + "," + getPriority() + "," +
                    "" + "]";
        }
    }


    /**
     * Sets the context ClassLoader for this Thread_. The context
     * ClassLoader can be set when a thread is created, and allows
     * the creator of the thread to provide the appropriate class loader,
     * through {@code getContextClassLoader}, to code running in the thread
     * when loading classes and resources.
     *
     * <p>If a security manager is present, its {@link
     * SecurityManager_#checkPermission(java.security.Permission) checkPermission}
     * method is invoked with a {@link RuntimePermission RuntimePermission}{@code
     * ("setContextClassLoader")} permission to see if setting the context
     * ClassLoader is permitted.
     *
     * @param  cl
     *         the context ClassLoader for this Thread_, or null  indicating the
     *         system class loader (or, failing that, the bootstrap class loader)
     *
     * @throws  SecurityException
     *          if the current thread cannot set the context ClassLoader
     *
     * @since 1.2
     */
    public void setContextClassLoader(ClassLoader cl) {
        SecurityManager_ sm = System_.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setContextClassLoader"));
        }
        contextClassLoader = cl;
    }

    /**
     * Returns <tt>true</tt> if and only if the current thread holds the
     * monitor lock on the specified object.
     *
     * <p>This method is designed to allow a program to assert that
     * the current thread already holds a specified lock:
     * <pre>
     *     assert Thread_.holdsLock(obj);
     * </pre>
     *
     * @param  obj the object on which to test lock ownership
     * @throws NullPointerException if obj is <tt>null</tt>
     * @return <tt>true</tt> if the current thread holds the monitor lock on
     *         the specified object.
     * @since 1.4
     */
    public static native boolean holdsLock(Object obj);

    private static final StackTraceElement[] EMPTY_STACK_TRACE
            = new StackTraceElement[0];

    /**
     * Returns an array of stack trace elements representing the stack dump
     * of this thread.  This method will return a zero-length array if
     * this thread has not started, has started but has not yet been
     * scheduled to run by the system, or has terminated.
     * If the returned array is of non-zero length then the first element of
     * the array represents the top of the stack, which is the most recent
     * method invocation in the sequence.  The last element of the array
     * represents the bottom of the stack, which is the least recent method
     * invocation in the sequence.
     *
     * <p>If there is a security manager, and this thread is not
     * the current thread, then the security manager's
     * <tt>checkPermission</tt> method is called with a
     * <tt>RuntimePermission("getStackTrace")</tt> permission
     * to see if it's ok to get the stack trace.
     *
     * <p>Some virtual machines may, under some circumstances, omit one
     * or more stack frames from the stack trace.  In the extreme case,
     * a virtual machine that has no stack trace information concerning
     * this thread is permitted to return a zero-length array from this
     * method.
     *
     * @return an array of <tt>StackTraceElement</tt>,
     * each represents one stack frame.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <tt>checkPermission</tt> method doesn't allow
     *        getting the stack trace of thread.
     * @see SecurityManager_#checkPermission
     * @see RuntimePermission
     * @see Throwable#getStackTrace
     *
     * @since 1.5
     */
    public StackTraceElement[] getStackTrace() {
        if (this != Thread_.currentThread()) {
            // check for getStackTrace permission
            SecurityManager_ security = System_.getSecurityManager();
            if (security != null) {
                security.checkPermission(
                        SecurityConstants.GET_STACK_TRACE_PERMISSION);
            }
            // optimization so we do not call into the vm for threads that
            // have not yet started or have terminated
            if (!isAlive()) {
                return EMPTY_STACK_TRACE;
            }
            StackTraceElement[][] stackTraceArray = dumpThreads(new Thread_[] {this});
            StackTraceElement[] stackTrace = stackTraceArray[0];
            // a thread that was alive during the previous isAlive call may have
            // since terminated, therefore not having a stacktrace.
            if (stackTrace == null) {
                stackTrace = EMPTY_STACK_TRACE;
            }
            return stackTrace;
        } else {
            // Don't need JVM help for current thread
            return (new Exception()).getStackTrace();
        }
    }

    /**
     * Returns a map of stack traces for all live threads.
     * The map keys are threads and each map value is an array of
     * <tt>StackTraceElement</tt> that represents the stack dump
     * of the corresponding <tt>Thread_</tt>.
     * The returned stack traces are in the format specified for
     * the {@link #getStackTrace getStackTrace} method.
     *
     * <p>The threads may be executing while this method is called.
     * The stack trace of each thread only represents a snapshot and
     * each stack trace may be obtained at different time.  A zero-length
     * array will be returned in the map value if the virtual machine has
     * no stack trace information about a thread.
     *
     * <p>If there is a security manager, then the security manager's
     * <tt>checkPermission</tt> method is called with a
     * <tt>RuntimePermission("getStackTrace")</tt> permission as well as
     * <tt>RuntimePermission("modifyThreadGroup")</tt> permission
     * to see if it is ok to get the stack trace of all threads.
     *
     * @return a <tt>Map</tt> from <tt>Thread_</tt> to an array of
     * <tt>StackTraceElement</tt> that represents the stack trace of
     * the corresponding thread.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <tt>checkPermission</tt> method doesn't allow
     *        getting the stack trace of thread.
     * @see #getStackTrace
     * @see SecurityManager_#checkPermission
     * @see RuntimePermission
     * @see Throwable#getStackTrace
     *
     * @since 1.5
     */
    public static Map_<Thread_, StackTraceElement[]> getAllStackTraces() {
        // check for getStackTrace permission
        SecurityManager_ security = System_.getSecurityManager();
        if (security != null) {
            security.checkPermission(
                    SecurityConstants.GET_STACK_TRACE_PERMISSION);
            security.checkPermission(
                    SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
        }

        // Get a snapshot of the list of all threads
        Thread_[] threads = getThreads();
        StackTraceElement[][] traces = dumpThreads(threads);
        Map_<Thread_, StackTraceElement[]> m = new HashMap_<>(threads.length);
        for (int i = 0; i < threads.length; i++) {
            StackTraceElement[] stackTrace = traces[i];
            if (stackTrace != null) {
                m.put(threads[i], stackTrace);
            }
            // else terminated so we don't put it in the map
        }
        return m;
    }


    private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION =
            new RuntimePermission("enableContextClassLoaderOverride");

    /** cache of subclass security audit results */
    /* Replace with ConcurrentReferenceHashMap when/if it appears in a future
     * release */
    private static class Caches {
        /** cache of subclass security audit results */
        static final ConcurrentMap<WeakClassKey,Boolean> subclassAudits =
                new ConcurrentHashMap<>();

        /** queue for WeakReferences to audited subclasses */
        static final ReferenceQueue<Class<?>> subclassAuditsQueue =
                new ReferenceQueue<>();
    }



    private native static StackTraceElement[][] dumpThreads(Thread_[] threads);
    private native static Thread_[] getThreads();

    /**
     * Returns the identifier of this Thread_.  The thread ID is a positive
     * <tt>long</tt> number generated when this thread was created.
     * The thread ID is unique and remains unchanged during its lifetime.
     * When a thread is terminated, this thread ID may be reused.
     *
     * @return this thread's ID.
     * @since 1.5
     */
    public long getId() {
        return tid;
    }



    /**
     * Returns the state of this thread.
     * This method is designed for use in monitoring of the system state,
     * not for synchronization control.
     *
     * @return this thread's state.
     * @since 1.5
     */
    public State getState() {
        // get current thread state
        if ((threadStatus & 4) != 0) {
            return State.RUNNABLE;
        } else if ((threadStatus & 1024) != 0) {
            return State.BLOCKED;
        } else if ((threadStatus & 16) != 0) {
            return State.WAITING;
        } else if ((threadStatus & 32) != 0) {
            return State.TIMED_WAITING;
        } else if ((threadStatus & 2) != 0) {
            return State.TERMINATED;
        } else {
            return (threadStatus & 1) == 0 ? State.NEW : State.RUNNABLE;
        }
    }

    // Added in JSR-166

    @FunctionalInterface
    public interface UncaughtExceptionHandler {
        /**
         * Method invoked when the given thread terminates due to the
         * given uncaught exception.
         * <p>Any exception thrown by this method will be ignored by the
         * Java Virtual Machine.
         * @param t the thread
         * @param e the exception
         */
        void uncaughtException(Thread_ t, Throwable e);
    }

    // null unless explicitly set
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;

    // null unless explicitly set
    private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
        SecurityManager_ sm = System_.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(
                    new RuntimePermission("setDefaultUncaughtExceptionHandler")
            );
        }

        defaultUncaughtExceptionHandler = eh;
    }


    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler(){
        return defaultUncaughtExceptionHandler;
    }

   
    /**
     * Removes from the specified map any keys that have been enqueued
     * on the specified reference queue.
     */
    static void processQueue(ReferenceQueue<Class<?>> queue,
                             ConcurrentMap<? extends
                                                                  WeakReference<Class<?>>, ?> map)
    {
        Reference<? extends Class<?>> ref;
        while((ref = queue.poll()) != null) {
            map.remove(ref);
        }
    }

    /**
     *  Weak key for Class objects.
     **/
    static class WeakClassKey extends WeakReference<Class<?>> {
        /**
         * saved value of the referent's identity hash code, to maintain
         * a consistent hash code after the referent has been cleared
         */
        private final int hash;

        /**
         * Create a new WeakClassKey to the given object, registered
         * with a queue.
         */
        WeakClassKey(Class<?> cl, ReferenceQueue<Class<?>> refQueue) {
            super(cl, refQueue);
            hash = System_.identityHashCode(cl);
        }

        /**
         * Returns the identity hash code of the original referent.
         */
        @Override
        public int hashCode() {
            return hash;
        }

        /**
         * Returns true if the given object is this identical
         * WeakClassKey instance, or, if this object's referent has not
         * been cleared, if the given object is another WeakClassKey
         * instance with the identical non-null referent as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (obj instanceof WeakClassKey) {
                Object referent = get();
                return (referent != null) &&
                        (referent == ((WeakClassKey) obj).get());
            } else {
                return false;
            }
        }
    }


    // The following three initially uninitialized fields are exclusively
    // managed by class java.util.concurrent.ThreadLocalRandom. These
    // fields are used to build the high-performance PRNGs in the
    // concurrent code, and we can not risk accidental false sharing.
    // Hence, the fields are isolated with @Contended.

    /** The current seed for a ThreadLocalRandom */
    @sun.misc.Contended("tlr")
    long threadLocalRandomSeed;

    /** Probe hash value; nonzero if threadLocalRandomSeed initialized */
    @sun.misc.Contended("tlr")
    int threadLocalRandomProbe;

    /** Secondary seed isolated from public ThreadLocalRandom sequence */
    @sun.misc.Contended("tlr")
    int threadLocalRandomSecondarySeed;

    /* Some private helper methods */
    private native void setPriority0(int newPriority);
    private native void stop0(Object o);
    private native void suspend0();
    private native void resume0();
    private native void interrupt0();
    private native void setNativeName(String name);
}

