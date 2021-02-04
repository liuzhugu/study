package org.liuzhugu.javastudy.course.commonfault.concurrent;

import lombok.Getter;


public class Data {
    @Getter
    private static int count;

    public static int reset() {
        count = 0;
        return count;
    }

    //该方法非静态   锁住的对象是实例   但不同实例能同时执行该方法
    public synchronized static void wrong() {
        count ++;
    }
}
