package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadlocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyThreadLocal<V> {

    private final Map<Thread,V> threadLocalMap = new ConcurrentHashMap<>();

    public V get() {
        return get(Thread.currentThread());
    }

    public V get(Thread t) {
        return threadLocalMap.get(t);
    }

    public void set(V value) {
        set(Thread.currentThread(),value);
    }

    public void set(Thread t,V value) {
        threadLocalMap.put(t,value);
    }
}
