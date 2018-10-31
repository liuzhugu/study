package org.liuzhugu.javastudy.practice.other.Threadcommunication;

import java.util.concurrent.CountDownLatch;

/**
 *  达到条件后意味着线程的结束
 * */
public class CountDownLatchStudy {

    public static void main(String[] args)throws InterruptedException{
        int thread=3;
        long start = System.currentTimeMillis();
        final CountDownLatch countDownLatch=new CountDownLatch(thread);
        for(int i=0;i<thread;i++){
            //匿名内部类
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("thread run");
                    try {
                        Thread.sleep(2000);
                        countDownLatch.countDown();
                        System.out.println("thread end");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },"thread"+i).start();
        }


        countDownLatch.await();
        long stop = System.currentTimeMillis();
        System.out.println("main over total time="+(stop-start));
    }
}
