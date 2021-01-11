package org.liuzhugu.javastudy.course.concurrrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MyThreadPool {

    //任务队列(存放工任务的地方)
    BlockingQueue<Runnable> workQueue;
    //工作线程(实际工作的人)
    List<WorkThread> threads = new ArrayList<>();

    public MyThreadPool(int poolSize,BlockingQueue<Runnable> workQueue){
        //创建工作队列
        this.workQueue = workQueue;
        //创建工作线程
        for(int i = 0;i < poolSize;i ++) {
            WorkThread thread = new WorkThread();
            thread.start();
            threads.add(thread);
        }
    }

    //提交任务
    void execute(Runnable command) {
        try {
            workQueue.put(command);
        }catch (Exception e){}
    }

    //疑惑:多出的任务怎么办
    public static void main(String[] args) {
        //创建有界阻塞队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(2);
        //初始化线程池
        MyThreadPool myThreadPool = new MyThreadPool(10,workQueue);
        //提交任务来执行
        myThreadPool.execute(() -> {System.out.println("hello");});
    }

    class WorkThread extends Thread {
        @Override
        public void run() {
            //每个工人不断去取任务执行
            while (true) {
                try {
                    Runnable task = workQueue.take();
                    task.run();
                }catch (Exception e){}
            }
        }
    }

}
