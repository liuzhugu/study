package org.liuzhugu.javastudy.book.logicjava.mapandset;

import org.liuzhugu.javastudy.sourcecode.jdk8.container.LinkedHashMap_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.Map_;

public class LRUCache<K,V> extends LinkedHashMap_<K,V> {

    private int maxEntries;
    public LRUCache(int maxEntries){
        super(16,0.75f,true);
        this.maxEntries=maxEntries;
    }

    @Override
    //复写该方法在容量满的时候返回true,来使插入数据之后删除最老的元素即head
    protected boolean removeEldestEntry(Map_.Entry<K, V> eldest) {
        return size()>maxEntries;
    }
}
