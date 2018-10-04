package org.liuzhugu.javastudy.understandingjvm;

import java.io.Serializable;

/**
 * Created by liuting6 on 2017/10/25.
 * 重载匹配的优先级
 */
public class Overload {
    //2
    public static void sayHello(int arg){
        System.out.println("hello,int");
    }
    //6
    public static void sayHello(Object arg){
        System.out.println("hello,object");
    }
    //3
    public static void sayHello(long arg){
        System.out.println("hello,long");
    }
    //4
    public static void sayHello(Character arg){
        System.out.println("hello,character");
    }
    //1
    public static void sayHello(char arg){
        System.out.println("hello,char");
    }
    //7
    public static void sayHello(char... arg){
        System.out.println("hello,char...");
    }
    //5
    public static void sayHello(Serializable arg){
        System.out.println("hello,Serializable");
    }
    public static void main(String[] args){
        //优先级顺序是
        //char->int->long->Character->Serializable->object->char...
        sayHello('a');
    }
}
