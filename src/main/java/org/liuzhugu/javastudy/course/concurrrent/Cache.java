package org.liuzhugu.javastudy.course.concurrrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<K,V> {
    //存放缓存
    final Map<K,V> cache = new HashMap<>();
    //读写锁,与Map组合起来提供线程安全的缓存
    final ReadWriteLock rtl = new ReentrantReadWriteLock();
    final Lock r = rtl.readLock();
    final Lock w = rtl.writeLock();

    public V getCache(K key) {
        V value = null;
        r.lock();
        try {
            value = cache.get(key);
        }finally {
            r.unlock();
        }
        if (value != null) {
            return value;
        }
        w.lock();
        try {
            value = cache.get(key);
            //双重锁判定
            if (value == null) {
                //查询缓存
                //value =
                cache.put(key,value);
            }
            //查询数据库

        }finally {
            w.unlock();
        }
        return value;
    }

}
