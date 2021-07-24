package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  主动模式的代理类，持久化存储访问文章的请求
 * */
public class AsyncRequestPersistence implements RequestPersistence{
    /**
     * 单例对象
     * */
    private static AsyncRequestPersistence INSTANCE = new AsyncRequestPersistence();

    /**
     * 获取实例对象
     *
     * @return 结果
     */
    public static AsyncRequestPersistence getInstance() {
        return INSTANCE;
    }

    /**
     * 任务调度线程池
     * */
    private final ThreadPoolExecutor scheduler;

    /**
     * 真正处理请求的地方
     * */
    private final DbRequestPersistence delegate = new DbRequestPersistence();

    /**
     * 初始化线程池
     * */
    private AsyncRequestPersistence() {
        this.scheduler = new ThreadPoolExecutor(1,
                3,
                60 * 60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                r -> new Thread(r,"AsyncRequestPersistence"));
        //拒绝策略
        // 谁来提交  被拒绝后谁执行当前任务
        this.scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void store(ArticleAccessInfo articleAccessInfo) {
        //封装成一个methodRequest
        //然后入队列

        Callable<Boolean> methodRequest = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                System.out.println("请求出队列执行  通过Servant角色来执行具体的methodRequest请求");
                delegate.store(articleAccessInfo);
                return Boolean.TRUE;
            }
        };
        System.out.println("封装一个methodRequest的请求  提交到scheduler的队列中");

        //叫methodRequest的请求如scheduler的队列
        scheduler.submit(methodRequest);
    }

    @Override
    public void close() throws IOException {

    }
}
