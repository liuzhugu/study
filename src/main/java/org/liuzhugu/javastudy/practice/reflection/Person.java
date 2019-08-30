package org.liuzhugu.javastudy.practice.reflection;


public class Person {

    public String name;

    protected String age;

    private String hobby;

    public Person(String name, String age, String hobby) {
        this.name = name;
        this.age = age;
        this.hobby = hobby;
    }

    public Person(){}

    public String getHobby(){
        return hobby;
    }
}

