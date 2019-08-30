
package org.liuzhugu.javastudy.sourcecode.jdk8.container.list;

import java.util.*;
import java.util.function.UnaryOperator;


public interface List_<E> extends Collection<E> {

    // 查询操作
    int size();
    boolean isEmpty();
    boolean contains(Object o);
    Iterator<E> iterator();
    Object[] toArray();
    <T> T[] toArray(T[] a);


    // 修改操作
    boolean add(E e);
    boolean remove(Object o);

    // 批量修改操作
    boolean containsAll(Collection<?> c);
    boolean addAll(Collection<? extends E> c);
    boolean addAll(int index, Collection<? extends E> c);
    boolean removeAll(Collection<?> c);
    boolean retainAll(Collection<?> c);
    default void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final ListIterator<E> li = this.listIterator();
        while (li.hasNext()) {
            li.set(operator.apply(li.next()));
        }
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }
    void clear();


    //比较和哈希
    boolean equals(Object o);
    int hashCode();


    // 位置访问操作
    E get(int index);
    E set(int index, E element);
    void add(int index, E element);
    E remove(int index);

    // 查找操作
    int indexOf(Object o);
    int lastIndexOf(Object o);

    // 遍历
    ListIterator<E> listIterator();
    ListIterator<E> listIterator(int index);

    // View
    List_<E> subList(int fromIndex, int toIndex);
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }
}
