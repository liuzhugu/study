package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;

import java.util.ArrayList;
import java.util.List;

public class Allocator {
    private List<Object> als = new ArrayList<>();

    boolean apply(Object from,Object to,String name,int index) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //双重判定
        if (als.contains(from) || als.contains(to)) {
            System.out.println(name + " 第 " + index + " 次尝试  获取锁失败");
            return false;
        }
        synchronized (this) {
            if (als.contains(from) || als.contains(to)) {
                System.out.println(name + " 第 " + index + " 次尝试  获取锁失败");
                return false;
            } else {
                System.out.println(name + " 第 " + index + " 次尝试  获取锁成功");

                //将获取锁的操作变为原子操作  那么只会要么全部锁获取成功  要么全部锁获取失败
                //不会出现只获取到部分锁的情况
                als.add(from);
                als.add(to);
            }
            return true;
        }
    }

    synchronized void clean(Object from,Object to) {
        als.remove(from);
        als.remove(to);
    }
}
