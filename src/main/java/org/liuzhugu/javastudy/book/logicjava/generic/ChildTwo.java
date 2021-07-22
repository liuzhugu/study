package org.liuzhugu.javastudy.book.logicjava.generic;

public class ChildTwo implements Super<String> {
    @Override
    public void sayHello(String s) {
        System.out.println("ChildTwo say hello to " + s);
    }
}
