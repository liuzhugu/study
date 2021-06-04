package org.liuzhugu.javastudy.course.concurrent;

/**
 * 单例模式
 * */
public class Singleton {
    //静态,类间共享
    private static Singleton singleton;

    //构造方法私有化,除了get方法无法获得对象
    private Singleton(){
        //饿汉式   一开始就创建
        singleton = new Singleton();
    }

    public static Singleton getInstance(){
        //懒汉式,真正需要时才创建
        if (singleton == null) {
            synchronized (Singleton.class) {
               if (singleton == null) {
                   singleton = new Singleton();
               }
            }
        }
        return singleton;
    }
}
