package org.liuzhugu.javastudy.framestudy.spring.ioc;

public class Cat implements Animal{

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void say(){
        System.out.println("I am " + name + "!");
    }
}
