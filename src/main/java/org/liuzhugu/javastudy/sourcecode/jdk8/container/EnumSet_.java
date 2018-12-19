

package org.liuzhugu.javastudy.sourcecode.jdk8.container;

import sun.misc.SharedSecrets;

import java.util.Collection;
import java.util.Iterator;


/**
 * 基于为向量实现,判断某个枚举值,根据枚举值的序数来确定相应的位置
 * */
public abstract class EnumSet_<E extends Enum<E>> extends AbstractSet_<E>
    implements Cloneable, java.io.Serializable
{

    final Class<E> elementType;


    final Enum<?>[] universe;

    private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];

    EnumSet_(Class<E>elementType, Enum<?>[] universe) {
        this.elementType = elementType;
        this.universe    = universe;
    }


    public static <E extends Enum<E>> EnumSet_<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            //long型变量,y有64个标志位,因此可以用于枚举值小于等于64的枚举类
            return new RegularEnumSet_<>(elementType, universe);
        else
            //long型变量数组,index除64取余之后才确定向量
            return new JumboEnumSet_<>(elementType, universe);
    }


    public static <E extends Enum<E>> EnumSet_<E> allOf(Class<E> elementType) {
        EnumSet_<E> result = noneOf(elementType);
        result.addAll();
        return result;
    }


    abstract void addAll();


    public static <E extends Enum<E>> EnumSet_<E> copyOf(EnumSet_<E> s) {
        return s.clone();
    }
    public static <E extends Enum<E>> EnumSet_<E> copyOf(Collection<E> c) {
        if (c instanceof EnumSet_) {
            return ((EnumSet_<E>)c).clone();
        } else {
            if (c.isEmpty())
                throw new IllegalArgumentException("Collection is empty");
            Iterator<E> i = c.iterator();
            E first = i.next();
            EnumSet_<E> result = EnumSet_.of(first);
            while (i.hasNext())
                result.add(i.next());
            return result;
        }
    }
    public static <E extends Enum<E>> EnumSet_<E> complementOf(EnumSet_<E> s) {
        EnumSet_<E> result = copyOf(s);
        result.complement();
        return result;
    }
    public static <E extends Enum<E>> EnumSet_<E> of(E e) {
        EnumSet_<E> result = noneOf(e.getDeclaringClass());
        result.add(e);
        return result;
    }
    public static <E extends Enum<E>> EnumSet_<E> of(E e1, E e2) {
        EnumSet_<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        return result;
    }
    public static <E extends Enum<E>> EnumSet_<E> of(E e1, E e2, E e3) {
        EnumSet_<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        return result;
    }
    public static <E extends Enum<E>> EnumSet_<E> of(E e1, E e2, E e3, E e4) {
        EnumSet_<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        return result;
    }
    public static <E extends Enum<E>> EnumSet_<E> of(E e1, E e2, E e3, E e4, E e5) {
        EnumSet_<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        result.add(e5);
        return result;
    }


    @SafeVarargs
    public static <E extends Enum<E>> EnumSet_<E> of(E first, E... rest) {
        EnumSet_<E> result = noneOf(first.getDeclaringClass());
        result.add(first);
        for (E e : rest)
            result.add(e);
        return result;
    }


    public static <E extends Enum<E>> EnumSet_<E> range(E from, E to) {
        if (from.compareTo(to) > 0)
            throw new IllegalArgumentException(from + " > " + to);
        EnumSet_<E> result = noneOf(from.getDeclaringClass());
        result.addRange(from, to);
        return result;
    }


    abstract void addRange(E from, E to);


    @SuppressWarnings("unchecked")
    public EnumSet_<E> clone() {
        try {
            return (EnumSet_<E>) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }


    abstract void complement();

    final void typeCheck(E e) {
        Class<?> eClass = e.getClass();
        if (eClass != elementType && eClass.getSuperclass() != elementType)
            throw new ClassCastException(eClass + " != " + elementType);
    }


    private static <E extends Enum<E>> E[] getUniverse(Class<E> elementType) {
        return SharedSecrets.getJavaLangAccess()
                                        .getEnumConstantsShared(elementType);
    }


    private static class SerializationProxy <E extends Enum<E>>
        implements java.io.Serializable
    {

        private final Class<E> elementType;

        private final Enum<?>[] elements;

        SerializationProxy(EnumSet_<E> set) {
            elementType = set.elementType;
            elements = set.toArray(ZERO_LENGTH_ENUM_ARRAY);
        }

        // instead of cast to E, we should perhaps use elementType.cast()
        // to avoid injection of forged stream, but it will slow the implementation
        @SuppressWarnings("unchecked")
        private Object readResolve() {
            EnumSet_<E> result = EnumSet_.noneOf(elementType);
            for (Enum<?> e : elements)
                result.add((E)e);
            return result;
        }

        private static final long serialVersionUID = 362491234563181265L;
    }

    Object writeReplace() {
        return new SerializationProxy<>(this);
    }

    // readObject method for the serialization proxy pattern
    // See Effective Java, Second Ed., Item 78.
    private void readObject(java.io.ObjectInputStream stream)
        throws java.io.InvalidObjectException {
        throw new java.io.InvalidObjectException("Proxy required");
    }
}
