package org.liuzhugu.javastudy.javaA.concurrency;

/**
 * Created by liuting6 on 2018/1/10.
 */
public class SuspendAndResume {
    private final static Object object=new Object();
    static class ThreadA extends Thread{
        public void run(){
            synchronized (object) {
                System.out.println("start...");
                Thread.currentThread().suspend();
                System.out.println("thread end...");
            }
        }
    }
    public static void main(String[] args)throws InterruptedException{
        ThreadA t1=new ThreadA();
        ThreadA t2=new ThreadA();
        t1.start();
        t2.start();
        Thread.sleep(100);
        System.out.println(t1.getState());
        System.out.println(t2.getState());
        if(t1.getState().compareTo(Thread.State.RUNNABLE)==0){
            t1.resume();
        }
        if(t2.getState().compareTo(Thread.State.RUNNABLE)==0){
            t2.resume();
        }
    }
}
