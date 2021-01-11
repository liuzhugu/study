package org.liuzhugu.javastudy.practice.concurrent;

/**
 * 双重锁判定
 * */
public class Singleton {

    //static保证只有一份,volate保证可见性
    private volatile static Singleton singleton;
    //确保无法通过new的方法创建对象
    private Singleton(){}
    public Singleton getInstance(){
        //懒汉式
        if (singleton == null) {
            synchronized (Singleton.class) {
                //需要重新判定，因为通过第一次判定之后不一定立马获得锁，所以获得锁之后到这里的时候
                //singleton不一定还为空，所以得再判空，
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
