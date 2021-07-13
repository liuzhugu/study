package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadpool;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;

public class ExecutorsStudy {
    public static void main(String[] args) {
        //1.只有一个线程的线程池
        Executors_.newSingleThreadExecutor();
        //2.线程数可变的线程池   理论最大线程数为Integer.MAX_VALUE
            //如果有线程空闲  那么优先使用空闲线程执行新任务   如果没有空闲线程才创建新线程执行新任务
        Executors_.newCachedThreadPool();
        //3.固定数量的线程池   线程数量固定  处理不过来只能放到任务队列里
        Executors_.newFixedThreadPool(10);
        //4.线程池大小为1的可定时调度的任务线程池
        Executors_.newSingleThreadScheduledExecutor();
        //5.可指定核心线程数的可定是调度的任务线程池
        Executors_.newScheduledThreadPool(10);
    }
}
