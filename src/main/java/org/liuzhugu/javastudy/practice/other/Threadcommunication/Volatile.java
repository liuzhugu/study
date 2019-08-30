package org.liuzhugu.javastudy.practice.other.Threadcommunication;

public class Volatile implements Runnable {

    private static volatile boolean flag;

    @Override
    public void run() {
        while(!flag){
            System.out.println(Thread.currentThread().getName() + "正在运行。。。");
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() +"执行完毕");
    }

    public static void main(String[] args)throws InterruptedException{
        Volatile aVolatile=new Volatile();
        new Thread(aVolatile,"thread A").start();

        System.out.println("main 线程正在运行") ;
        Thread.sleep(5000);
        aVolatile.stopThread();
    }

    private void stopThread(){
        flag=true;
    }
}
