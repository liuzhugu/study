package org.liuzhugu.javastudy.book.logicjava.chapter2;

/**
 * Created by liuting6 on 2018/1/30.
 */
public class SingletonDemo1 {
//    //饿汉式
//    private static SingletonDemo1 onlyOne=new SingletonDemo1();
//    private SingletonDemo1(){}   //除了get方法，无法创建实例
//    public SingletonDemo1 get(){
//        return onlyOne;
//    }
    //懒汉式
    private static SingletonDemo1 onlyOne;
    private SingletonDemo1(){}
    private static boolean isCreate=false;
    public SingletonDemo1 get(){
        if(isCreate==false){
            synchronized (SingletonDemo1.class){
                if(isCreate==false){
                    onlyOne=new SingletonDemo1();
                    isCreate=true;
                }
            }
        }
        return onlyOne;
    }
}
