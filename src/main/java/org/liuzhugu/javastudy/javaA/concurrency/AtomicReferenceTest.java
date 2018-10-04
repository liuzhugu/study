package org.liuzhugu.javastudy.javaA.concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuting6 on 2018/1/15.
 * 引用的原子修改
 */
public class AtomicReferenceTest {
    //public final static AtomicReference<String> ATOMIC_REFERENCE=new AtomicReference<String>("abc");
    public final static AtomicStampedReference<String> ATOMIC_REFERENCE=new AtomicStampedReference<String>("abc",0);
    private final static Random RANDOM_OBJECT=new Random();
    public static void main(String[] args)throws InterruptedException{
        final CountDownLatch startCountDown=new CountDownLatch(1);
        Thread[] threads=new Thread[20];
        for(int i=0;i<20;i++){
            final int num=i;
            threads[i]=new Thread(){
                public void run(){
                    String oldValue=ATOMIC_REFERENCE.getReference();
                    int stamp=ATOMIC_REFERENCE.getStamp();
                    try{
                        startCountDown.await();
                        Thread.sleep(RANDOM_OBJECT.nextInt()&100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(ATOMIC_REFERENCE.compareAndSet(oldValue,oldValue+num,stamp,stamp+1)){
                        System.out.println("线程:"+num+",进行了对象修改");
                    }
//                    if(ATOMIC_REFERENCE.compareAndSet(oldValue,oldValue+num)){
//                        System.out.println("线程:"+num+",进行了对象修改");
//                    }
                }
            };
            threads[i].start();
        }
        Thread.sleep(200);
        startCountDown.countDown();
        new Thread(){
          public void run(){
              try{
                  ReentrantLock test=new ReentrantLock();
                  test.lock();
                  Thread.sleep(RANDOM_OBJECT.nextInt()&200);
              }catch (InterruptedException e){
                  e.printStackTrace();
              }
              int stamp=ATOMIC_REFERENCE.getStamp();
              while(!ATOMIC_REFERENCE.compareAndSet(ATOMIC_REFERENCE.getReference(),"abc",stamp,stamp+1));
              System.out.println("已经改为原始值");

          }
        }.start();
//        for(Thread thread:threads){
//            thread.join();
//        }
//        System.out.println("最终结果为:"+ATOMIC_REFERENCE.get());
    }
}
