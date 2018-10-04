package org.liuzhugu.javastudy.understandingjvm;

/**
 * Created by liuting6 on 2017/10/25.
 * 动态分派
 */
public class DynamicDispatch {
    static  class Human{
        protected  void sayHello(){
            System.out.println("hello,guy");
        };
        protected void sayHello(int a){
            System.out.println(a);
        }
    }
    static class Man extends Human{

        protected void sayHello(){
            System.out.println("hello,man!");
        }
    }
    static class Woman extends Human{

        protected void sayHello(){
            System.out.println("hello,woman!");
        }
    }
    public static void main(String[] args){
        Human man=new Man();
        Human woman=new Woman();
        man.sayHello(1);
        man.sayHello();
        woman.sayHello();
        man=woman;
        man.sayHello();
    }
}
