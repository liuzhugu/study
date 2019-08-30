package org.liuzhugu.javastudy.practice.other.Threadcommunication;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
/**
 *  达到条件后意味着线程的开始
 * */
public class CyclicBarrierStudy {
    public static void main(String[] args){
        final CyclicBarrier cyclicBarrier=new CyclicBarrier(3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1 run");
                try {
                    cyclicBarrier.await();
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("thread1 end do something");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2 run");
                try {
                    cyclicBarrier.await();
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("thread2 end do something");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread3 run");
                try {
                    Thread.sleep(5000);
                    cyclicBarrier.await();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("thread3 end do something");
            }
        }).start();

    }
}
