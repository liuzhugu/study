package org.liuzhugu.javastudy.book.javaA.concurrency;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuting6 on 2018/1/23.
 */
public class SemaphoreByWaitNotify {
    private final static int QUERY_MAX_LENGTH=5;
    private final static int THREAD_COUNT=20;
    private final static AtomicInteger NOW_CALL_COUNT=new AtomicInteger(0);
    private final static Object LOCK_OBJECT=new Object();
    private static void waitForObjectNotify(){
        synchronized (LOCK_OBJECT){
            try{
                LOCK_OBJECT.wait(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    private static void tryToLock(){
        int tryTimes=0;
        int nowValue=NOW_CALL_COUNT.get();
        while(true){
            //自旋CAS取锁，超过一定次数休眠，被唤醒后继续尝试，最终失败超过次数退出
            if(nowValue<QUERY_MAX_LENGTH&&NOW_CALL_COUNT.compareAndSet(nowValue,nowValue+1)){
                break;      //获得锁跳出循环
            }
            if(tryTimes%3==0){
                waitForObjectNotify();
            }
            nowValue=NOW_CALL_COUNT.get();
            tryTimes++;
        }
    }
    private static void tryToUnlock(){
        NOW_CALL_COUNT.getAndDecrement();
        synchronized (LOCK_OBJECT){
            LOCK_OBJECT.notify();
        }
    }
    public static void main(String[] args){
        final Random random=new Random();
        
        for(int i=0;i<THREAD_COUNT;i++){
            new Thread(String.valueOf(i)){
                public void run(){
                    tryToLock();//尝试获得执行权限
                    System.out.println(this.getName()+"======我要开始做操作了!");
                    try{
                        Thread.sleep(100+(random.nextInt()&3000));
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println(this.getName()+"======操作结束了!");
                    tryToUnlock();
                }
            }.start();
        }
    }
}
