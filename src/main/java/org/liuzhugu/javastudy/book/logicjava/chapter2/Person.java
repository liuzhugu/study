package org.liuzhugu.javastudy.book.logicjava.chapter2;

/**
 * Created by liuting6 on 2018/1/30.
 */
public class Person {
    private String name;
    private Person father;
    private Person mother;
    private Person[] child;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public Person[] getChild() {
        return child;
    }

    public void setChild(Person[] child) {
        this.child = child;
    }

    public Person(String name){
        this.name=name;
    }
    public static void main(String[] args){
        Person laoliu=new Person("laoliu");
        Person xiaoliu=new Person("xiaoliu");
        xiaoliu.setFather(laoliu);
        laoliu.setChild(new Person[]{xiaoliu});
        System.out.println(xiaoliu.getFather().getName());

    }
}
