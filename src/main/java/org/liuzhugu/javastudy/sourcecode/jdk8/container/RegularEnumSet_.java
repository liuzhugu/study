/*
 * Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package org.liuzhugu.javastudy.sourcecode.jdk8.container;

import java.util.*;

class RegularEnumSet_<E extends Enum<E>> extends EnumSet_<E> {
    private static final long serialVersionUID = 3411599620347842686L;

    /**
     * long型有64位,因此最多支持有64个枚举值的枚举类
     * */
    private long elements = 0L;

    /**
     * 重要API
     * */
    public int size() {
        //计算为1的位有多少
        return Long.bitCount(elements);
    }
    public boolean add(E e) {
        typeCheck(e);
        long oldElements = elements;
        //把e按序数值右移之后与向量或,即把相应位置为1,则相当于把e加入set中
        elements |= (1L << ((Enum<?>)e).ordinal());
        return elements != oldElements;
    }
    public boolean remove(Object e) {
        if (e == null)
            return false;
        Class<?> eClass = e.getClass();
        if (eClass != elementType && eClass.getSuperclass() != elementType)
            return false;

        long oldElements = elements;
        //把e按序数值右移之后取反再与向量与,即把相应位置为0,则相当于把e从set中去掉
        elements &= ~(1L << ((Enum<?>)e).ordinal());
        return elements != oldElements;
    }
    public boolean contains(Object e) {
        if (e == null)
            return false;
        Class<?> eClass = e.getClass();
        if (eClass != elementType && eClass.getSuperclass() != elementType)
            return false;

        return (elements & (1L << ((Enum<?>)e).ordinal())) != 0;
    }
    //求补集
    void complement() {
        if (universe.length != 0) {
            //按位取反,这样就得到了补集
            elements = ~elements;
            //但这样的话没用到的地方都变成了1,因此把那些位重新置0
            elements &= -1L >>> -universe.length;  // Mask unused bits
        }
    }



    void addRange(E from, E to) {
        elements = (-1L >>>  (from.ordinal() - to.ordinal() - 1)) << from.ordinal();
    }
    void addAll() {
        if (universe.length != 0)
            elements = -1L >>> -universe.length;
    }
    RegularEnumSet_(Class<E>elementType, Enum<?>[] universe) {
        super(elementType, universe);
    }



    public Iterator<E> iterator() {
        return new EnumSetIterator<>();
    }

    private class EnumSetIterator<E extends Enum<E>> implements Iterator<E> {

        long unseen;


        long lastReturned = 0;

        EnumSetIterator() {
            unseen = elements;
        }

        public boolean hasNext() {
            return unseen != 0;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (unseen == 0)
                throw new NoSuchElementException();
            lastReturned = unseen & -unseen;
            unseen -= lastReturned;
            return (E) universe[Long.numberOfTrailingZeros(lastReturned)];
        }

        public void remove() {
            if (lastReturned == 0)
                throw new IllegalStateException();
            elements &= ~lastReturned;
            lastReturned = 0;
        }
    }


    public boolean isEmpty() {
        return elements == 0;
    }




    // Modification Operations



    // Bulk Operations


    public boolean containsAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet_))
            return super.containsAll(c);

        RegularEnumSet_<?> es = (RegularEnumSet_<?>)c;
        if (es.elementType != elementType)
            return es.isEmpty();

        return (es.elements & ~elements) == 0;
    }


    public boolean addAll(Collection<? extends E> c) {
        if (!(c instanceof RegularEnumSet_))
            return super.addAll(c);

        RegularEnumSet_<?> es = (RegularEnumSet_<?>)c;
        if (es.elementType != elementType) {
            if (es.isEmpty())
                return false;
            else
                throw new ClassCastException(
                    es.elementType + " != " + elementType);
        }

        long oldElements = elements;
        elements |= es.elements;
        return elements != oldElements;
    }


    public boolean removeAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet_))
            return super.removeAll(c);

        RegularEnumSet_<?> es = (RegularEnumSet_<?>)c;
        if (es.elementType != elementType)
            return false;

        long oldElements = elements;
        elements &= ~es.elements;
        return elements != oldElements;
    }


    public boolean retainAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet_))
            return super.retainAll(c);

        RegularEnumSet_<?> es = (RegularEnumSet_<?>)c;
        if (es.elementType != elementType) {
            boolean changed = (elements != 0);
            elements = 0;
            return changed;
        }

        long oldElements = elements;
        elements &= es.elements;
        return elements != oldElements;
    }


    public void clear() {
        elements = 0;
    }


    public boolean equals(Object o) {
        if (!(o instanceof RegularEnumSet_))
            return super.equals(o);

        RegularEnumSet_<?> es = (RegularEnumSet_<?>)o;
        if (es.elementType != elementType)
            return elements == 0 && es.elements == 0;
        return es.elements == elements;
    }
}
