package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import sun.misc.VM;

import java.io.PrintStream;
import java.util.Arrays;

public class ThreadGroup_ implements Thread_.UncaughtExceptionHandler {
    private final ThreadGroup_ parent;
    String name;
    int maxPriority;
    boolean destroyed;
    boolean daemon;
    boolean vmAllowSuspension;

    int nUnstartedThreads = 0;
    int nthreads;
    Thread_ threads[];

    int ngroups;
    ThreadGroup_ groups[];

    /**
     * Creates an empty Thread_ group that is not in any Thread_ group.
     * This method is used to create the system Thread_ group.
     */
    private ThreadGroup_() {     // called from C code
        this.name = "system";
        this.maxPriority = Thread_.MAX_PRIORITY;
        this.parent = null;
    }

    /**
     * Constructs a new Thread_ group. The parent of this new group is
     * the Thread_ group of the currently running Thread_.
     * <p>
     * The <code>checkAccess</code> method of the parent Thread_ group is
     * called with no arguments; this may result in a security exception.
     *
     * @param   name   the name of the new Thread_ group.
     * @exception  SecurityException  if the current Thread_ cannot create a
     *               Thread_ in the specified Thread_ group.
     * @since   JDK1.0
     */
    public ThreadGroup_(String name) {
        this(Thread_.currentThread().getThreadGroup(), name);
    }

    /**
     * Creates a new Thread_ group. The parent of this new group is the
     * specified Thread_ group.
     * <p>
     * The <code>checkAccess</code> method of the parent Thread_ group is
     * called with no arguments; this may result in a security exception.
     *
     * @param     parent   the parent Thread_ group.
     * @param     name     the name of the new Thread_ group.
     * @exception  NullPointerException  if the Thread_ group argument is
     *               <code>null</code>.
     * @exception  SecurityException  if the current Thread_ cannot create a
     *               Thread_ in the specified Thread_ group.
     * @see     java.lang.SecurityException
     * @since   JDK1.0
     */
    public ThreadGroup_(ThreadGroup_ parent, String name) {
        this(checkParentAccess(parent), parent, name);
    }

    private ThreadGroup_(Void unused, ThreadGroup_ parent, String name) {
        this.name = name;
        this.maxPriority = parent.maxPriority;
        this.daemon = parent.daemon;
        this.vmAllowSuspension = parent.vmAllowSuspension;
        this.parent = parent;
        parent.add(this);
    }

    /*
     * @throws  NullPointerException  if the parent argument is {@code null}
     * @throws  SecurityException     if the current Thread_ cannot create a
     *                                Thread_ in the specified Thread_ group.
     */
    private static Void checkParentAccess(ThreadGroup_ parent) {
        parent.checkAccess();
        return null;
    }

    /**
     * Returns the name of this Thread_ group.
     *
     * @return  the name of this Thread_ group.
     * @since   JDK1.0
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the parent of this Thread_ group.
     * <p>
     * First, if the parent is not <code>null</code>, the
     * <code>checkAccess</code> method of the parent Thread_ group is
     * called with no arguments; this may result in a security exception.
     *
     * @return  the parent of this Thread_ group. The top-level Thread_ group
     *          is the only Thread_ group whose parent is <code>null</code>.
     * @exception  SecurityException  if the current Thread_ cannot modify
     *               this Thread_ group.
     * @see        java.lang.SecurityException
     * @see        java.lang.RuntimePermission
     * @since   JDK1.0
     */
    public final ThreadGroup_ getParent() {
        if (parent != null)
            parent.checkAccess();
        return parent;
    }

    /**
     * Returns the maximum priority of this Thread_ group. Threads that are
     * part of this group cannot have a higher priority than the maximum
     * priority.
     *
     * @return  the maximum priority that a Thread_ in this Thread_ group
     *          can have.
     * @see     #setMaxPriority
     * @since   JDK1.0
     */
    public final int getMaxPriority() {
        return maxPriority;
    }

    /**
     * Tests if this Thread_ group is a daemon Thread_ group. A
     * daemon Thread_ group is automatically destroyed when its last
     * Thread_ is stopped or its last Thread_ group is destroyed.
     *
     * @return  <code>true</code> if this Thread_ group is a daemon Thread_ group;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public final boolean isDaemon() {
        return daemon;
    }

    /**
     * Tests if this Thread_ group has been destroyed.
     *
     * @return  true if this object is destroyed
     * @since   JDK1.1
     */
    public synchronized boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Changes the daemon status of this Thread_ group.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * A daemon Thread_ group is automatically destroyed when its last
     * Thread_ is stopped or its last Thread_ group is destroyed.
     *
     * @param      daemon   if <code>true</code>, marks this Thread_ group as
     *                      a daemon Thread_ group; otherwise, marks this
     *                      Thread_ group as normal.
     * @exception  SecurityException  if the current Thread_ cannot modify
     *               this Thread_ group.
     * @see        java.lang.SecurityException
     * @since      JDK1.0
     */
    public final void setDaemon(boolean daemon) {
        checkAccess();
        this.daemon = daemon;
    }

    /**
     * Sets the maximum priority of the group. Threads in the Thread_
     * group that already have a higher priority are not affected.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * If the <code>pri</code> argument is less than
     * {@link Thread_#MIN_PRIORITY} or greater than
     * {@link Thread_#MAX_PRIORITY}, the maximum priority of the group
     * remains unchanged.
     * <p>
     * Otherwise, the priority of this ThreadGroup_ object is set to the
     * smaller of the specified <code>pri</code> and the maximum permitted
     * priority of the parent of this Thread_ group. (If this Thread_ group
     * is the system Thread_ group, which has no parent, then its maximum
     * priority is simply set to <code>pri</code>.) Then this method is
     * called recursively, with <code>pri</code> as its argument, for
     * every Thread_ group that belongs to this Thread_ group.
     *
     * @param      pri   the new priority of the Thread_ group.
     * @exception  SecurityException  if the current Thread_ cannot modify
     *               this Thread_ group.
     * @see        #getMaxPriority
     * @see        java.lang.SecurityException
     * @since      JDK1.0
     */
    public final void setMaxPriority(int pri) {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            if (pri < Thread_.MIN_PRIORITY || pri > Thread_.MAX_PRIORITY) {
                return;
            }
            maxPriority = (parent != null) ? Math.min(pri, parent.maxPriority) : pri;
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].setMaxPriority(pri);
        }
    }

    /**
     * Tests if this Thread_ group is either the Thread_ group
     * argument or one of its ancestor Thread_ groups.
     *
     * @param   g   a Thread_ group.
     * @return  <code>true</code> if this Thread_ group is the Thread_ group
     *          argument or one of its ancestor Thread_ groups;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public final boolean parentOf(ThreadGroup_ g) {
        for (; g != null ; g = g.parent) {
            if (g == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the currently running Thread_ has permission to
     * modify this Thread_ group.
     * <p>
     * If there is a security manager, its <code>checkAccess</code> method
     * is called with this Thread_ group as its argument. This may result
     * in throwing a <code>SecurityException</code>.
     *
     * @exception  SecurityException  if the current Thread_ is not allowed to
     *               access this Thread_ group.
     * @since      JDK1.0
     */
    public final void checkAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            //security.checkAccess(this);
        }
    }

    /**
     * Returns an estimate of the number of active threads in this Thread_
     * group and its subgroups. Recursively iterates over all subgroups in
     * this Thread_ group.
     *
     * <p> The value returned is only an estimate because the number of
     * threads may change dynamically while this method traverses internal
     * data structures, and might be affected by the presence of certain
     * system threads. This method is intended primarily for debugging
     * and monitoring purposes.
     *
     * @return  an estimate of the number of active threads in this Thread_
     *          group and in any other Thread_ group that has this Thread_
     *          group as an ancestor
     *
     * @since   JDK1.0
     */
    public int activeCount() {
        int result;
        // Snapshot sub-group data so we don't hold this lock
        // while our children are computing.
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            result = nthreads;
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            result += groupsSnapshot[i].activeCount();
        }
        return result;
    }

    /**
     * Copies into the specified array every active Thread_ in this
     * Thread_ group and its subgroups.
     *
     * <p> An invocation of this method behaves in exactly the same
     * way as the invocation
     *
     * <blockquote>
     * {@linkplain #enumerate(Thread_[], boolean) enumerate}{@code (list, true)}
     * </blockquote>
     *
     * @param  list
     *         an array into which to put the list of threads
     *
     * @return  the number of threads put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current Thread_ cannot access this Thread_ group
     *
     * @since   JDK1.0
     */
    public int enumerate(Thread_ list[]) {
        checkAccess();
        return enumerate(list, 0, true);
    }

    /**
     * Copies into the specified array every active Thread_ in this
     * Thread_ group. If {@code recurse} is {@code true},
     * this method recursively enumerates all subgroups of this
     * Thread_ group and references to every active Thread_ in these
     * subgroups are also included. If the array is too short to
     * hold all the threads, the extra threads are silently ignored.
     *
     * <p> An application might use the {@linkplain #activeCount activeCount}
     * method to get an estimate of how big the array should be, however
     * <i>if the array is too short to hold all the threads, the extra threads
     * are silently ignored.</i>  If it is critical to obtain every active
     * Thread_ in this Thread_ group, the caller should verify that the returned
     * int value is strictly less than the length of {@code list}.
     *
     * <p> Due to the inherent race condition in this method, it is recommended
     * that the method only be used for debugging and monitoring purposes.
     *
     * @param  list
     *         an array into which to put the list of threads
     *
     * @param  recurse
     *         if {@code true}, recursively enumerate all subgroups of this
     *         Thread_ group
     *
     * @return  the number of threads put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current Thread_ cannot access this Thread_ group
     *
     * @since   JDK1.0
     */
    public int enumerate(Thread_ list[], boolean recurse) {
        checkAccess();
        return enumerate(list, 0, recurse);
    }

    private int enumerate(Thread_ list[], int n, boolean recurse) {
        int ngroupsSnapshot = 0;
        ThreadGroup_[] groupsSnapshot = null;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            int nt = nthreads;
            if (nt > list.length - n) {
                nt = list.length - n;
            }
            for (int i = 0; i < nt; i++) {
                if (threads[i].isAlive()) {
                    list[n++] = threads[i];
                }
            }
            if (recurse) {
                ngroupsSnapshot = ngroups;
                if (groups != null) {
                    groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
                } else {
                    groupsSnapshot = null;
                }
            }
        }
        if (recurse) {
            for (int i = 0 ; i < ngroupsSnapshot ; i++) {
                n = groupsSnapshot[i].enumerate(list, n, true);
            }
        }
        return n;
    }

    /**
     * Returns an estimate of the number of active groups in this
     * Thread_ group and its subgroups. Recursively iterates over
     * all subgroups in this Thread_ group.
     *
     * <p> The value returned is only an estimate because the number of
     * Thread_ groups may change dynamically while this method traverses
     * internal data structures. This method is intended primarily for
     * debugging and monitoring purposes.
     *
     * @return  the number of active Thread_ groups with this Thread_ group as
     *          an ancestor
     *
     * @since   JDK1.0
     */
    public int activeGroupCount() {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        int n = ngroupsSnapshot;
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            n += groupsSnapshot[i].activeGroupCount();
        }
        return n;
    }

    /**
     * Copies into the specified array references to every active
     * subgroup in this Thread_ group and its subgroups.
     *
     * <p> An invocation of this method behaves in exactly the same
     * way as the invocation
     *
     * <blockquote>
     * {@linkplain #enumerate(ThreadGroup_[], boolean) enumerate}{@code (list, true)}
     * </blockquote>
     *
     * @param  list
     *         an array into which to put the list of Thread_ groups
     *
     * @return  the number of Thread_ groups put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current Thread_ cannot access this Thread_ group
     *
     * @since   JDK1.0
     */
    public int enumerate(ThreadGroup_ list[]) {
        checkAccess();
        return enumerate(list, 0, true);
    }

    /**
     * Copies into the specified array references to every active
     * subgroup in this Thread_ group. If {@code recurse} is
     * {@code true}, this method recursively enumerates all subgroups of this
     * Thread_ group and references to every active Thread_ group in these
     * subgroups are also included.
     *
     * <p> An application might use the
     * {@linkplain #activeGroupCount activeGroupCount} method to
     * get an estimate of how big the array should be, however <i>if the
     * array is too short to hold all the Thread_ groups, the extra Thread_
     * groups are silently ignored.</i>  If it is critical to obtain every
     * active subgroup in this Thread_ group, the caller should verify that
     * the returned int value is strictly less than the length of
     * {@code list}.
     *
     * <p> Due to the inherent race condition in this method, it is recommended
     * that the method only be used for debugging and monitoring purposes.
     *
     * @param  list
     *         an array into which to put the list of Thread_ groups
     *
     * @param  recurse
     *         if {@code true}, recursively enumerate all subgroups
     *
     * @return  the number of Thread_ groups put into the array
     *
     * @throws  SecurityException
     *          if {@linkplain #checkAccess checkAccess} determines that
     *          the current Thread_ cannot access this Thread_ group
     *
     * @since   JDK1.0
     */
    public int enumerate(ThreadGroup_ list[], boolean recurse) {
        checkAccess();
        return enumerate(list, 0, recurse);
    }

    private int enumerate(ThreadGroup_ list[], int n, boolean recurse) {
        int ngroupsSnapshot = 0;
        ThreadGroup_[] groupsSnapshot = null;
        synchronized (this) {
            if (destroyed) {
                return 0;
            }
            int ng = ngroups;
            if (ng > list.length - n) {
                ng = list.length - n;
            }
            if (ng > 0) {
                System.arraycopy(groups, 0, list, n, ng);
                n += ng;
            }
            if (recurse) {
                ngroupsSnapshot = ngroups;
                if (groups != null) {
                    groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
                } else {
                    groupsSnapshot = null;
                }
            }
        }
        if (recurse) {
            for (int i = 0 ; i < ngroupsSnapshot ; i++) {
                n = groupsSnapshot[i].enumerate(list, n, true);
            }
        }
        return n;
    }

    /**
     * Stops all threads in this Thread_ group.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>stop</code> method on all the
     * threads in this Thread_ group and in all of its subgroups.
     *
     * @exception  SecurityException  if the current Thread_ is not allowed
     *               to access this Thread_ group or any of the threads in
     *               the Thread_ group.
     * @see        java.lang.SecurityException
     * @since      JDK1.0
     * @deprecated    This method is inherently unsafe.  See
     *     {@link Thread_#stop} for details.
     */
    @Deprecated
    public final void stop() {
        if (stopOrSuspend(false))
            Thread_.currentThread().stop();
    }

    /**
     * Interrupts all threads in this Thread_ group.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>interrupt</code> method on all the
     * threads in this Thread_ group and in all of its subgroups.
     *
     * @exception  SecurityException  if the current Thread_ is not allowed
     *               to access this Thread_ group or any of the threads in
     *               the Thread_ group.
     * @see        java.lang.SecurityException
     * @since      1.2
     */
    public final void interrupt() {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                threads[i].interrupt();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].interrupt();
        }
    }

    /**
     * Suspends all threads in this Thread_ group.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>suspend</code> method on all the
     * threads in this Thread_ group and in all of its subgroups.
     *
     * @exception  SecurityException  if the current Thread_ is not allowed
     *               to access this Thread_ group or any of the threads in
     *               the Thread_ group.
     * @see        java.lang.SecurityException
     * @since      JDK1.0
     * @deprecated    This method is inherently deadlock-prone.  See
     *     {@link Thread_#suspend} for details.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public final void suspend() {
        if (stopOrSuspend(true))
            Thread_.currentThread().suspend();
    }

    /**
     * Helper method: recursively stops or suspends (as directed by the
     * boolean argument) all of the threads in this Thread_ group and its
     * subgroups, except the current Thread_.  This method returns true
     * if (and only if) the current Thread_ is found to be in this Thread_
     * group or one of its subgroups.
     */
    @SuppressWarnings("deprecation")
    private boolean stopOrSuspend(boolean suspend) {
        boolean suicide = false;
        Thread_ us = Thread_.currentThread();
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot = null;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                if (threads[i]==us)
                    suicide = true;
                else if (suspend)
                    threads[i].suspend();
                else
                    threads[i].stop();
            }

            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++)
            suicide = groupsSnapshot[i].stopOrSuspend(suspend) || suicide;

        return suicide;
    }

    /**
     * Resumes all threads in this Thread_ group.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     * <p>
     * This method then calls the <code>resume</code> method on all the
     * threads in this Thread_ group and in all of its sub groups.
     *
     * @exception  SecurityException  if the current Thread_ is not allowed to
     *               access this Thread_ group or any of the threads in the
     *               Thread_ group.
     * @see        java.lang.SecurityException
     * @since      JDK1.0
     * @deprecated    This method is used solely in conjunction with
     *      <tt>Thread_.suspend</tt> and <tt>ThreadGroup_.suspend</tt>,
     *       both of which have been deprecated, as they are inherently
     *       deadlock-prone.  See {@link Thread_#suspend} for details.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public final void resume() {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            for (int i = 0 ; i < nthreads ; i++) {
                threads[i].resume();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].resume();
        }
    }

    /**
     * Destroys this Thread_ group and all of its subgroups. This Thread_
     * group must be empty, indicating that all threads that had been in
     * this Thread_ group have since stopped.
     * <p>
     * First, the <code>checkAccess</code> method of this Thread_ group is
     * called with no arguments; this may result in a security exception.
     *
     * @exception  IllegalThreadStateException  if the Thread_ group is not
     *               empty or if the Thread_ group has already been destroyed.
     * @exception  SecurityException  if the current Thread_ cannot modify this
     *               Thread_ group.
     * @since      JDK1.0
     */
    public final void destroy() {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            checkAccess();
            if (destroyed || (nthreads > 0)) {
                throw new IllegalThreadStateException();
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
            if (parent != null) {
                destroyed = true;
                ngroups = 0;
                groups = null;
                nthreads = 0;
                threads = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i += 1) {
            groupsSnapshot[i].destroy();
        }
        if (parent != null) {
            parent.remove(this);
        }
    }

    /**
     * Adds the specified Thread_ group to this group.
     * @param g the specified Thread_ group to be added
     * @exception IllegalThreadStateException If the Thread_ group has been destroyed.
     */
    private final void add(ThreadGroup_ g){
        synchronized (this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            if (groups == null) {
                groups = new ThreadGroup_[4];
            } else if (ngroups == groups.length) {
                groups = Arrays.copyOf(groups, ngroups * 2);
            }
            groups[ngroups] = g;

            // This is done last so it doesn't matter in case the
            // Thread_ is killed
            ngroups++;
        }
    }

    /**
     * Removes the specified Thread_ group from this group.
     * @param g the Thread_ group to be removed
     * @return if this Thread_ has already been destroyed.
     */
    private void remove(ThreadGroup_ g) {
        synchronized (this) {
            if (destroyed) {
                return;
            }
            for (int i = 0 ; i < ngroups ; i++) {
                if (groups[i] == g) {
                    ngroups -= 1;
                    System.arraycopy(groups, i + 1, groups, i, ngroups - i);
                    // Zap dangling reference to the dead group so that
                    // the garbage collector will collect it.
                    groups[ngroups] = null;
                    break;
                }
            }
            if (nthreads == 0) {
                notifyAll();
            }
            if (daemon && (nthreads == 0) &&
                    (nUnstartedThreads == 0) && (ngroups == 0))
            {
                destroy();
            }
        }
    }


    /**
     * Increments the count of unstarted threads in the Thread_ group.
     * Unstarted threads are not added to the Thread_ group so that they
     * can be collected if they are never started, but they must be
     * counted so that daemon Thread_ groups with unstarted threads in
     * them are not destroyed.
     */
    void addUnstarted() {
        synchronized(this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            nUnstartedThreads++;
        }
    }

    /**
     * Adds the specified Thread_ to this Thread_ group.
     *
     * <p> Note: This method is called from both library code
     * and the Virtual Machine. It is called from VM to add
     * certain system threads to the system Thread_ group.
     *
     * @param  t
     *         the Thread_ to be added
     *
     * @throws  IllegalThreadStateException
     *          if the Thread_ group has been destroyed
     */
    void add(Thread_ t) {
        synchronized (this) {
            if (destroyed) {
                throw new IllegalThreadStateException();
            }
            if (threads == null) {
                threads = new Thread_[4];
            } else if (nthreads == threads.length) {
                threads = Arrays.copyOf(threads, nthreads * 2);
            }
            threads[nthreads] = t;

            // This is done last so it doesn't matter in case the
            // Thread_ is killed
            nthreads++;

            // The Thread_ is now a fully fledged member of the group, even
            // though it may, or may not, have been started yet. It will prevent
            // the group from being destroyed so the unstarted Threads count is
            // decremented.
            nUnstartedThreads--;
        }
    }

    /**
     * Notifies the group that the Thread_ {@code t} has failed
     * an attempt to start.
     *
     * <p> The state of this Thread_ group is rolled back as if the
     * attempt to start the Thread_ has never occurred. The Thread_ is again
     * considered an unstarted member of the Thread_ group, and a subsequent
     * attempt to start the Thread_ is permitted.
     *
     * @param  t
     *         the Thread_ whose start method was invoked
     */
    void threadStartFailed(Thread_ t) {
        synchronized(this) {
            remove(t);
            nUnstartedThreads++;
        }
    }

    /**
     * Notifies the group that the Thread_ {@code t} has terminated.
     *
     * <p> Destroy the group if all of the following conditions are
     * true: this is a daemon Thread_ group; there are no more alive
     * or unstarted threads in the group; there are no subgroups in
     * this Thread_ group.
     *
     * @param  t
     *         the Thread_ that has terminated
     */
    void threadTerminated(Thread_ t) {
        synchronized (this) {
            remove(t);

            if (nthreads == 0) {
                notifyAll();
            }
            if (daemon && (nthreads == 0) &&
                    (nUnstartedThreads == 0) && (ngroups == 0))
            {
                destroy();
            }
        }
    }

    /**
     * Removes the specified Thread_ from this group. Invoking this method
     * on a Thread_ group that has been destroyed has no effect.
     *
     * @param  t
     *         the Thread_ to be removed
     */
    private void remove(Thread_ t) {
        synchronized (this) {
            if (destroyed) {
                return;
            }
            for (int i = 0 ; i < nthreads ; i++) {
                if (threads[i] == t) {
                    System.arraycopy(threads, i + 1, threads, i, --nthreads - i);
                    // Zap dangling reference to the dead Thread_ so that
                    // the garbage collector will collect it.
                    threads[nthreads] = null;
                    break;
                }
            }
        }
    }

    /**
     * Prints information about this Thread_ group to the standard
     * output. This method is useful only for debugging.
     *
     * @since   JDK1.0
     */
    public void list() {
        list(System.out, 0);
    }
    void list(PrintStream out, int indent) {
        int ngroupsSnapshot;
        ThreadGroup_[] groupsSnapshot;
        synchronized (this) {
            for (int j = 0 ; j < indent ; j++) {
                out.print(" ");
            }
            out.println(this);
            indent += 4;
            for (int i = 0 ; i < nthreads ; i++) {
                for (int j = 0 ; j < indent ; j++) {
                    out.print(" ");
                }
                out.println(threads[i]);
            }
            ngroupsSnapshot = ngroups;
            if (groups != null) {
                groupsSnapshot = Arrays.copyOf(groups, ngroupsSnapshot);
            } else {
                groupsSnapshot = null;
            }
        }
        for (int i = 0 ; i < ngroupsSnapshot ; i++) {
            groupsSnapshot[i].list(out, indent);
        }
    }

    /**
     * Called by the Java Virtual Machine when a Thread_ in this
     * Thread_ group stops because of an uncaught exception, and the Thread_
     * does not have a specific {@link Thread_.UncaughtExceptionHandler}
     * installed.
     * <p>
     * The <code>uncaughtException</code> method of
     * <code>ThreadGroup_</code> does the following:
     * <ul>
     * <li>If this Thread_ group has a parent Thread_ group, the
     *     <code>uncaughtException</code> method of that parent is called
     *     with the same two arguments.
     * <li>Otherwise, this method checks to see if there is a
     *     {@linkplain Thread_#getDefaultUncaughtExceptionHandler default
     *     uncaught exception handler} installed, and if so, its
     *     <code>uncaughtException</code> method is called with the same
     *     two arguments.
     * <li>Otherwise, this method determines if the <code>Throwable</code>
     *     argument is an instance of {@link ThreadDeath}. If so, nothing
     *     special is done. Otherwise, a message containing the
     *     Thread_'s name, as returned from the Thread_'s {@link
     *     Thread_#getName getName} method, and a stack backtrace,
     *     using the <code>Throwable</code>'s {@link
     *     Throwable#printStackTrace printStackTrace} method, is
     *     printed to the {@linkplain System#err standard error stream}.
     * </ul>
     * <p>
     * Applications can override this method in subclasses of
     * <code>ThreadGroup_</code> to provide alternative handling of
     * uncaught exceptions.
     *
     * @param   t   the Thread_ that is about to exit.
     * @param   e   the uncaught exception.
     * @since   JDK1.0
     */
    public void uncaughtException(Thread_ t, Throwable e) {
        if (parent != null) {
            parent.uncaughtException(t, e);
        } else {
            Thread_.UncaughtExceptionHandler ueh =
                    Thread_.getDefaultUncaughtExceptionHandler();
            if (ueh != null) {
                ueh.uncaughtException(t, e);
            } else if (!(e instanceof ThreadDeath)) {
                System.err.print("Exception in Thread_ \""
                        + t.getName() + "\" ");
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Used by VM to control lowmem implicit suspension.
     *
     * @param b boolean to allow or disallow suspension
     * @return true on success
     * @since   JDK1.1
     * @deprecated The definition of this call depends on {@link #suspend},
     *             which is deprecated.  Further, the behavior of this call
     *             was never specified.
     */
    @Deprecated
    public boolean allowThreadSuspension(boolean b) {
        this.vmAllowSuspension = b;
        if (!b) {
            VM.unsuspendSomeThreads();
        }
        return true;
    }

    /**
     * Returns a string representation of this Thread_ group.
     *
     * @return  a string representation of this Thread_ group.
     * @since   JDK1.0
     */
    public String toString() {
        return getClass().getName() + "[name=" + getName() + ",maxpri=" + maxPriority + "]";
    }
}

