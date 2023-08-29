package org.liuzhugu.javastudy.practice.tool;

import java.util.*;
import java.util.zip.CRC32;

/**
 * 一致性哈希
 * */
public class HashRing {
    //存放哈希值对应的节点
    private Map<Integer,String> nodes;
    //副本数    那么多个hash值会对应同一个key
    private int replicates;
    //存放所有节点的哈希值   组成一个环
    private List<Integer> keys;

    // 传入需要创建的副本数
    public HashRing(int replicates) {
        this.replicates = replicates;
        nodes = new HashMap<>();
        keys = new ArrayList<>();
    }

    //在哈希环上增加节点  需要传入节点名称
    public void add(String nodeName) {
        //根据副本数   在节点名称后添加数字后缀进行哈希计算  并放置节点
        CRC32 c = new CRC32();
        for (int i = 0;i < replicates;i ++) {
            c.reset();
            //构建节点的副本
            String s = nodeName + "-" + i;
            c.update(s.getBytes());
            //得到哈希值
            int hash = (int)c.getValue();
            //在哈希环中放入该副本节点
            keys.add(hash);
            //不同副本节点指向该节点
            nodes.put(hash,nodeName);
        }
        //为了便于查找节点  将环上的节点排序  因为哈希值随机  所以排序后  所有的key也相当于任意分配
        Collections.sort(keys);
    }

    //基于key在环上查找最近的节点
    public String get(String key) {
        CRC32 c = new CRC32();
        c.update(key.getBytes());

        int hash = (int) c.getValue();
        int index = 0;
        for (;index < keys.size();index ++) {
            if (keys.get(index) >= hash) {
                break;
            }
        }
        //环可以回到开头
        if (index == keys.size()) {
            index = 0;
        }
        return nodes.get(keys.get(index));
    }
}
