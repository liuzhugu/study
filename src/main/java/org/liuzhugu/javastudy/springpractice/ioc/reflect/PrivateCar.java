package org.liuzhugu.javastudy.springpractice.ioc.reflect;

public class PrivateCar {

    //private成员变量:使用传统的类示例调用方式的话，只能在该类内访问
    private String color;

    //protected:只能在子类或本包中访问
    protected void drive(){
        System.out.println("driver private car!the color is:"+color);
    }


}
