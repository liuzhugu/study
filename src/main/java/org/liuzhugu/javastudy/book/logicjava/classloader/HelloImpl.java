package org.liuzhugu.javastudy.book.logicjava.classloader;

public class HelloImpl implements IHelloService{

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sayhello() {
        name = "origin";
        System.out.println("helloImpl " + name);
    }
    public static void main(String[] args) {

    }
}
