package org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 不可变类
 *  不可变的是对象本身 但变量指向的对象还是可变的
 *  因为对象不可变   变量指向的对象要么是原对象   要么是新对象   不会指向一个被部分修改的对象
 *  这样就不会发生并发冲突了
 * */

//不能继承   这样子类无法覆写getter方法
public final class Location {
    //private  让子类及其他地方无法通过引用直接修改变量值
    //值不可修改   只能整个对象替换掉  这样就保证了修改的原子性
    private final double x;
    private final double y;
    private List<Integer> data;
    public Location(double x,double y,List<Integer> data) {
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<Integer> getData() {
        //如果返回引用的话  那么外部可以通过这个引用直接修改不可变类的字段
        //因此拷贝一个副本   与不可变类字段隔离开来  避免影响到内部
        return Collections.unmodifiableList(new ArrayList<>(data));
    }

    //不可变类值不可修改
//    public void setX(double x) {
//        this.x = x;
//    }
//    public void setY(double y) {
//        this.y = y;
//    }
}
