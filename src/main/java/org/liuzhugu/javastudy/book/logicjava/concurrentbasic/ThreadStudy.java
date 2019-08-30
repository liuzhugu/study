package org.liuzhugu.javastudy.book.logicjava.concurrentbasic;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Runnable_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Thread_;

public class ThreadStudy {

    public static void main(String[] args) {
        //创建线程的两种方式

        //1.继承Thread,这样的话跟Thread强耦合,但比较简单
        Thread_ helloThreadOne =  new HelloThreadOne();
        helloThreadOne.start();
        //2.实现Runnable接口,只需实现run方法,比较灵活
        Runnable_ runnable = new HelloThreadSecond();
        Thread_ helloThreadSecond = new Thread_(runnable);
        helloThreadSecond.start();

        //start方法调用的时候才会变成一条执行流,在start方法里面新建线程,分配资源,所有准备工作都准备好之后才会执行run方法
    }
}
class HelloThreadOne extends Thread_ {
    @Override
    public void run() {
        System.out.println("HelloThreadOne say hello to you");
    }
}
class HelloThreadSecond implements Runnable_ {
    @Override
    public void run() {
        System.out.println("HelloThreadSecond say hello to you");
    }
}

