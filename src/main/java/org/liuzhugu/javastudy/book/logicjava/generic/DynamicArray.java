package org.liuzhugu.javastudy.book.logicjava.generic;

import java.util.Arrays;

/**
 * 动态数组
 */
public class DynamicArray<E> {
    //默认容量
    private final static int DEFAULT_CAPACITY = 10;
    //当前已用
    private int size;
    //存放数据的数组
    private Object[] elementData;
    public DynamicArray() {
        this.elementData = new Object[DEFAULT_CAPACITY];
    }
    //扩容
    private void ensureCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        if (oldCapacity >= minCapacity) {
            return;
        }
        //容量不够  扩容
        int newCapacity = oldCapacity * 2;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        elementData = Arrays.copyOf(elementData,newCapacity);
    }
    //增加
    public void add(E e) {
        //判断容量
        ensureCapacity(size + 1);
        elementData[size ++] = e;
    }
    //获取
    public E get(int index) {

        return (E)elementData[index];
    }
    public int size() {
        return this.size;
    }
    //设置
    public E set(int index,E e) {
        E oldValue = (E)elementData[index];
        elementData[index] = e;
        return oldValue;
    }
    /**
     * 容器内可以放入它任意子类的元素
     * 泛型类内的泛型参数在实例化之后可以容纳其任意子类
     * 但被实例化为不同类型的泛型类却都是并列的  原本的参数类型之间的关系不影响
     * */
    public  void addAll(DynamicArray<? extends E> c) {
        for (int i = 0;i < c.size;i ++) {
            add(c.get(i));
        }
    }

    /**
     *  声明为父类的变量可以指向子类 但声明为子类的变量却不能指向父类 因此不管上届还是下届始终都是为了让父类变量存放子类对象
     *  如果如果是进来  那么进来的是该泛型类所实例化的类型的子类  该类型参数才能存放进来的变量 也就是该类型参数成为上界
     *  如果是出去    那么必须是该泛型类锁实例化的类型的父类   进来的变量才能存放该类型参数   也就是该类型参数成为下界
     * */
    public void copyTo(DynamicArray<? super E> dest) {
        for (int i = 0;i < size;i ++) {
            dest.add(get(i));
        }
    }

    public static <T extends Comparable<T>> T max(DynamicArray<T> arr) {
        T max = arr.get(0);
        for (int i = 1;i < arr.size();i ++) {
            if (arr.get(i).compareTo(max) > 0) {
                max = arr.get(i);
            }
        }
        return max;
    }
}
