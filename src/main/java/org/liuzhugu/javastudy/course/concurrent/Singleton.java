package org.liuzhugu.javastudy.course.concurrent;

/**
 * 单例模式
 * */
public class Singleton {
    //静态,类间共享
    private static Singleton INSTANCE = new Singleton();

    //构造方法私有化,除了get方法无法获得对象
    private Singleton(){
    }

    public static Singleton getInstance(){
        //懒汉式,真正需要时才创建
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                //双重锁判定  获取到锁的时候  有可能单例已经创建了
                // 所以要再次判断
               if (INSTANCE == null) {
                   INSTANCE = new Singleton();
               }
            }
        }
        return INSTANCE;
    }
}
