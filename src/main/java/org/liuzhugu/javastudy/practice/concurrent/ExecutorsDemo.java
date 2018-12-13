package org.liuzhugu.javastudy.practice.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ExecutorsDemo {

    //不要使用默认实现创建线程池，否则会出现无限创建任务或线程的情况
    //private static ExecutorService executor = Executors.newFixedThreadPool(15);

    //使用ThreadPoolExecutor,在创建的时候指定容量
//    private static ExecutorService executor=new ThreadPoolExecutor(10,10,60L,
//            TimeUnit.SECONDS,new ArrayBlockingQueue(10));

    //guava ThreadFactoryBuilder
    private static ThreadFactory nameFactory =  new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();

    private static ExecutorService executor = new ThreadPoolExecutor(5,200,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1024),nameFactory,new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args){
        for(int i=0;i<Integer.MAX_VALUE;i++){
            executor.execute(new SubThread());
        }
    }
}

class SubThread implements Runnable{
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        }catch (Exception e){
            //do nothing
        }
    }
}
