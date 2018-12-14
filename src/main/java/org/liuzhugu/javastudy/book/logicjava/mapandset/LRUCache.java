package org.liuzhugu.javastudy.book.logicjava.mapandset;

import org.liuzhugu.javastudy.sourcecode.jdk8.container.LinkedHashMapS;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.MapS;

public class LRUCache<K,V> extends LinkedHashMapS<K,V>{

    private int maxEntries;
    public LRUCache(int maxEntries){
        super(16,0.75f,true);
        this.maxEntries=maxEntries;
    }

    @Override
    //复写该方法在容量满的时候返回true,来使插入数据之后删除最老的元素即head
    protected boolean removeEldestEntry(MapS.Entry<K, V> eldest) {
        return size()>maxEntries;
    }
}
