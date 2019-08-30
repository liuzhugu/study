package org.liuzhugu.javastudy.practice.other.Threadcommunication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorStop {
    public static void main(String[] args)throws Exception{
        BlockingQueue<Runnable> queue=new LinkedBlockingDeque<>(10);
        ThreadPoolExecutor poolExecutor=new ThreadPoolExecutor(5,5,1, TimeUnit.MILLISECONDS,queue);

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("running");
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("running2");
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //前提,之后线程池会停止接受新任务，并且会平滑的关闭线程池中现有的任务
        poolExecutor.shutdown();
        while(!poolExecutor.awaitTermination(1,TimeUnit.SECONDS)){
            System.out.println("线程还在执行。。。");
        }
        System.out.println("main over");
    }
}
