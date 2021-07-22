package org.liuzhugu.javastudy.book.logicjava.generic;

public class ChildOne<T> implements Super<T> {
    @Override
    public void sayHello(T t) {
        System.out.println("ChildOne say hello to " + t);
    }
}
