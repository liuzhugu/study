package org.liuzhugu.javastudy.javaA.concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liuting6 on 2018/1/23.
 */
public class CountDownLatchTest {
    private final static int GROUP_SIZE=5;
    private final static Random random=new Random();
    public static void main(String[] args)throws InterruptedException{
        processOnOneGroup("分组1");
        processOnOneGroup("分组2");
    }
    private static void processOnOneGroup(final String groupName)throws InterruptedException{
        final CountDownLatch preCountDownLatch=new CountDownLatch(GROUP_SIZE);
        final CountDownLatch startCountDownLatch=new CountDownLatch(1);
        final CountDownLatch endCountDownLatch=new CountDownLatch(5);
        System.out.println("====》\n分组:"+groupName+"比赛开始:");
        for(int i=0;i<GROUP_SIZE;i++){
            new Thread(String.valueOf(i)){
                public void run(){
                    preCountDownLatch.countDown();//代表准备就绪
                    System.out.println("我是线程组:["+groupName+"],第"+this.getName()+" 号线程，我已准备就绪");
                    try {
                        //等待裁判发出开始命令
                        startCountDownLatch.await();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    try {
                        //模拟一段运行时间
                        Thread.sleep(Math.abs(random.nextInt())%1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println("我是线程组:["+groupName+"],第"+this.getName()+" 号线程，我已执行完毕");
                    endCountDownLatch.countDown();
                }
            }.start();
        }
        preCountDownLatch.await();//等待所有队员就位
        System.out.println("各就各位，准备");
        startCountDownLatch.countDown();//开始比赛
        try{
            endCountDownLatch.await();//等待多个赛跑者逐个结束
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(groupName+"比赛结束");
    }
}
