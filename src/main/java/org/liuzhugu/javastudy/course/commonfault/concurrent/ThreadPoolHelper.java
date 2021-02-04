package org.liuzhugu.javastudy.course.commonfault.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolHelper {
    //只创建一个  共享
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            //核心线程数   最大线程数
            10,50,
            //存活时间
            2, TimeUnit.SECONDS,
            //任务队列
            new ArrayBlockingQueue<>(10),
            //线程工厂    定制线程名称
            new ThreadFactoryBuilder().setNameFormat("demo-threadpool-%d").get(),
            //拒绝策略
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}
