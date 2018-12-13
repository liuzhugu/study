package org.liuzhugu.javastudy.book.javaA.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuting6 on 2018/1/22.
 */
public class LockTest {
    private static List<Integer> arrayList=new ArrayList<>();
    public static void main(String[] args)throws InterruptedException{
        final LockTest test=new LockTest();
        new Thread(){
            public void run(){
                test.insert(Thread.currentThread());
            }
        }.start();
        new Thread(){
            public void run(){
                test.insert(Thread.currentThread());
            }
        }.start();
        Thread.sleep(100);
        for(Integer i:arrayList){
            System.out.println(i);
        }
    }
    public void insert(Thread thread){
        Lock lock=new ReentrantLock();
        lock.lock();
        try{
            System.out.println(thread.getName()+" 获得了锁");
            for(int i=0;i<5;i++){
                arrayList.add(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println(thread.getName()+" 释放了锁");
            lock.unlock();
        }
    }
}
