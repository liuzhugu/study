package org.liuzhugu.javastudy.practice.other.Threadcommunication;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * 两个线程交替打印奇偶数
 * */
public class TwoThreadWaitNotify {

    private int start=1;

    private boolean flag=false;

    public static void main(String[] args)throws Exception{
        //wait notify
        TwoThreadWaitNotify number=new TwoThreadWaitNotify();
        Thread t1=new Thread(new OuNum(number));
        t1.setName("A");
        Thread t2=new Thread(new JiNum(number));
        t2.setName("B");
        t1.start();
        t2.start();

        //join
        //join();
    }

    private static void join() throws InterruptedException{
        //join
        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t3 running");
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("t3 end");

            }
        });
        Thread t4=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t4 running");
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("t4 end");
            }
        });
        t3.setName("A");
        t3.start();
        t4.setName("B");
        t4.start();

        t3.join();
        t4.join();
    }

    public static class OuNum implements Runnable{

        private TwoThreadWaitNotify number;

        public OuNum(TwoThreadWaitNotify number) {
            this.number = number;
        }

        @Override
        public void run() {
            while(number.start<=100){
                synchronized (TwoThreadWaitNotify.class){
                    //System.out.println("偶数线程抢到锁了");
                    if(number.flag){
                        System.out.println(Thread.currentThread().getName() + "+-+偶数" + number.start);
                        number.start++;
                        number.flag=false;
                        TwoThreadWaitNotify.class.notify();
                    }else {
                        try {
                            TwoThreadWaitNotify.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static class JiNum implements Runnable{

        private TwoThreadWaitNotify number;

        public JiNum(TwoThreadWaitNotify number) {
            this.number = number;
        }

        @Override
        public void run() {
            while(number.start<=100){
                synchronized (TwoThreadWaitNotify.class){
                    //System.out.println("奇数线程抢到锁了");
                    if(!number.flag){
                        System.out.println(Thread.currentThread().getName() + "+-+奇数" + number.start);
                        number.start++;
                        number.flag=true;
                        TwoThreadWaitNotify.class.notify();
                    }else {
                        try {
                            TwoThreadWaitNotify.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
