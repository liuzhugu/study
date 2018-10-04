package org.liuzhugu.javastudy.javaA.concurrency;

import java.util.Random;

/**
 * Created by liuting6 on 2018/1/9.
 */
public class SimpleThread {
    public static void main(String[] args){
        new Thread(){
            public void run(){
                System.out.println("现在执行的线程是 "+Thread.currentThread().getName());
                System.out.println("我是被创建的线程，我开始执行了...");
            }
        }.start();
        //主线程继续执行，上面的start方法调用后会调用本地方法创建一个新的线程，在新的线程里面执行run方法，因为这需要时间，所以主线程先执行完
        System.out.println("现在执行的线程是 "+Thread.currentThread().getName());
        System.out.println("main process end..");
    }
}
