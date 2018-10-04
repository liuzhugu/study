package org.liuzhugu.javastudy.understandingjvm;

/**
 * Created by liuting6 on 2017/10/25.
 * 静态分派
 */
public class StaticDIspatch {
    static abstract class Human{}
    static class Man extends Human{}
    static class Woman extends Human{}
    public void sayHello(Human human){
        System.out.println("hello,guy!");
    }
    public void sayHello(Man man){
        System.out.println("hello,gentleman!");
    }
    public void sayHello(Woman woman){
        System.out.println("hello,lady!");
    }
    public static void main(String[] args){
        Human man=new Man();
        Human woman=new Woman();
        StaticDIspatch sd=new StaticDIspatch();
        //输出:hello,guy! hello,guy!
        //重载是根据静态类型来确定具体的调用的方法的
        sd.sayHello(man);
        sd.sayHello(woman);
    }
}
