package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;


public class ReferenceQueue_<T> {

    /**
     * Constructs a new Reference_-object queue.
     */
    public ReferenceQueue_() { }

    private static class Null<S> extends ReferenceQueue_<S> {
        boolean enqueue(Reference_<? extends S> r) {
            return false;
        }
    }

    static ReferenceQueue_<Object> NULL = new ReferenceQueue_.Null<>();
    static ReferenceQueue_<Object> ENQUEUED = new ReferenceQueue_.Null<>();

    static private class Lock { };
    private ReferenceQueue_.Lock lock = new ReferenceQueue_.Lock();
    private volatile Reference_<? extends T> head = null;
    private long queueLength = 0;

    boolean enqueue(Reference_<? extends T> r) { /* Called only by Reference_ class */
        synchronized (lock) {
            // Check that since getting the lock this Reference_ hasn't already been
            // enqueued (and even then removed)
            ReferenceQueue_<?> queue = r.queue;
            if ((queue == NULL) || (queue == ENQUEUED)) {
                return false;
            }
            assert queue == this;
            r.queue = ENQUEUED;
            r.next = (head == null) ? r : head;
            head = r;
            queueLength++;
           
            lock.notifyAll();
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private Reference_<? extends T> reallyPoll() {       /* Must hold lock */
        Reference_<? extends T> r = head;
        if (r != null) {
            head = (r.next == r) ?
                    null :
                    r.next; // Unchecked due to the next field having a raw type in Reference_
            r.queue = NULL;
            r.next = r;
            queueLength--;
            return r;
        }
        return null;
    }

    /**
     * Polls this queue to see if a Reference_ object is available.  If one is
     * available without further delay then it is removed from the queue and
     * returned.  Otherwise this method immediately returns <tt>null</tt>.
     *
     * @return  A Reference_ object, if one was immediately available,
     *          otherwise <code>null</code>
     */
    public Reference_<? extends T> poll() {
        if (head == null)
            return null;
        synchronized (lock) {
            return reallyPoll();
        }
    }

    public Reference_<? extends T> remove(long timeout)
            throws IllegalArgumentException, InterruptedException
    {
        if (timeout < 0) {
            throw new IllegalArgumentException("Negative timeout value");
        }
        synchronized (lock) {
            Reference_<? extends T> r = reallyPoll();
            if (r != null) return r;
            long start = (timeout == 0) ? 0 : System.nanoTime();
            for (;;) {
                lock.wait(timeout);
                r = reallyPoll();
                if (r != null) return r;
                if (timeout != 0) {
                    long end = System.nanoTime();
                    timeout -= (end - start) / 1000_000;
                    if (timeout <= 0) return null;
                    start = end;
                }
            }
        }
    }


    public Reference_<? extends T> remove() throws InterruptedException {
        return remove(0);
    }

}
