package org.liuzhugu.javastudy.sourcecode.jdk8.container.set;

import java.util.*;

public interface Set_<E> extends Collection<E>{

    // 查询操作
    int size();
    boolean isEmpty();
    boolean contains(Object o);
    Iterator<E> iterator();
    Object[] toArray();
    <T> T[] toArray(T[] a);

    //修改操作
    boolean add(E e);
    boolean remove(Object o);

    // 批量操作
    boolean containsAll(Collection<?> c);
    boolean addAll(Collection<? extends E> c);
    /**
     * 保留,即求交集
     * */
    boolean retainAll(Collection<?> c);
    boolean removeAll(Collection<?> c);
    void clear();


    // 对比和哈希
    boolean equals(Object o);
    int hashCode();
}
