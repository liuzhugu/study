package org.liuzhugu.javastudy.sourcecode.jdk8.container;

import java.util.Collection;


public interface Queue_<E> extends Collection<E> {
    boolean add(E e);
    boolean offer(E e);
    E remove();
    E poll();
    E element();
    E peek();
}
