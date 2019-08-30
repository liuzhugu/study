package org.liuzhugu.javastudy.practice.other.Threadcommunication;

/**
 * 线程响应中断
 * */
public class StopThread implements Runnable{
    @Override
    public void run() {
        //对该终端标志做了判断，该标志才会起作用，如果对该标志置之不理的话，那么无法中断该线程
        while (!Thread.currentThread().isInterrupted()){
            System.out.println(Thread.currentThread().getName() + "运行中。。");
        }
        System.out.println(Thread.currentThread().getName() + "退出。。");
    }

    public static void main(String[] args)throws Exception{
        Thread thread=new Thread(new StopThread());
        thread.start();
        Thread.sleep(3000);
        thread.interrupt();
    }
}
