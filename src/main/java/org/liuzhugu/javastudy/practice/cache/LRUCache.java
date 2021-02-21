package org.liuzhugu.javastudy.practice.cache;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCache {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));       // 返回  1
        cache.put(3, 3);    // 该操作会使得密钥 2 作废
        System.out.println(cache.get(2));       // 返回 -1 (未找到)
        cache.put(4, 4);    // 该操作会使得密钥 1 作废
        System.out.println(cache.get(1));       // 返回 -1 (未找到)
        System.out.println(cache.get(3));       // 返回  3
        System.out.println(cache.get(4));       // 返回  4
    }

    //容量
    private int capacity;
    //当前数量
    private int size;
    //访问顺序  因为key唯一  所以存放key
    private Deque<Integer> visitOrder;
    //存放数据
    Map<Integer,Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.visitOrder = new LinkedList<>();
        this.cache = new HashMap<>(capacity);
        this.size = 0;
    }

    public int get(int key) {
        if (cache.get(key) == null) {
            return -1;
        } else {
            //将该数据提到头部 表示最近被访问是该数据
            visitOrder.remove(key);
            visitOrder.addFirst(key);
            return cache.get(key);
        }
    }

    public void put(int key,int value) {
        //如果已存在  那么是更新
        if (cache.get(key) != null) {
            //将该数据提到头部 表示最近被访问是该数据
            visitOrder.remove(key);
            visitOrder.addFirst(key);
            cache.put(key,value);
        }
        //否则是增加
        else {
            //增加就要考虑容量问题
            if (size == capacity) {
                //删掉最久未被访问的
                int deleteKey = visitOrder.removeLast();
                size --;
                cache.remove(deleteKey);
            }
            //新增
            visitOrder.addFirst(key);
            size ++;
            cache.put(key,value);
        }
    }
}
