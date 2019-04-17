package org.liuzhugu.javastudy.sourcecode.jdk8.container.set;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public interface NavigableSet_<E> extends SortedSet_<E> {
    E lower(E var1);

    E floor(E var1);

    E ceiling(E var1);

    E higher(E var1);

    E pollFirst();

    E pollLast();

    Iterator<E> iterator();

    NavigableSet_<E> descendingSet();

    Iterator<E> descendingIterator();

    NavigableSet_<E> subSet(E var1, boolean var2, E var3, boolean var4);

    NavigableSet_<E> headSet(E var1, boolean var2);

    NavigableSet_<E> tailSet(E var1, boolean var2);

    SortedSet_<E> subSet(E var1, E var2);

    SortedSet_<E> headSet(E var1);

    SortedSet_<E> tailSet(E var1);
}
