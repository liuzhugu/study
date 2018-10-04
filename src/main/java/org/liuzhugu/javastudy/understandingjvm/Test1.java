package org.liuzhugu.javastudy.understandingjvm;

/**
 * Created by liuting6 on 2017/10/24.
 * 非法向前访问
 */
public class Test1 {
    static {
        //i=0;                               //可以赋值
        //System.out.println(i);             //无法访问
    }
    public static int i;
    static class Parent{
        public static int A=1;
        static{
            A=2;
        }
    }
    static class Child extends Parent{
        public static int B=A;
    }
    public static void main(String[] args){
        //System.out.println(Child.B);
        Runnable script=new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread()+" start");
                DeadLoopClass dlc=new DeadLoopClass();
                System.out.println(Thread.currentThread()+" end");
            }
        };
        Thread thread1=new Thread(script);
        Thread thread2=new Thread(script);
        thread1.start();
        thread2.start();
        //两个线程，一个死循环不退出，一个进不去
    }
    static class DeadLoopClass{
        static {
            if(true){
                System.out.println(Thread.currentThread()+"init DeadLoopClass");
                while(true){
                }
            }
        }
    }
}
