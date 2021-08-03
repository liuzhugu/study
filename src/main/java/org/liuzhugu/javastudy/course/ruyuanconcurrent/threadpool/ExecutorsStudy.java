package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadpool;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;

import java.util.concurrent.*;

public class ExecutorsStudy {

    //适合IO密集型的线程池
    private static final ExecutorService ORDER_SYNC_THREAD_POOL =
            new ThreadPoolExecutor(
                    //核心线程尽量和机器的CPU数符合
                    Runtime.getRuntime().availableProcessors() + 1,
                    //因为是IO密集型  所以可以多创建线程应对任务较多的情况
                    Runtime.getRuntime().availableProcessors() * 2,
                    60,
                    TimeUnit.SECONDS,
                    //指定容量的有界队列
                    new LinkedBlockingQueue<>(500),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            //个性化的线程名称   更容易定位
                            Thread thread = new Thread(r,"sync-order-info-thread-pool");
                            thread.setDaemon(true);
                            return thread;
                        }
                    },
                    //只要线程还没有关闭  那么直接在提交任务的用户线程中运行当前任务
                    // 这样做任务 不会被丢弃   但是可能阻塞用户线程上的其他任务   造成业务性能下降
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    public static void main(String[] args) {
        //1.只有一个线程的线程池
        Executors.newSingleThreadExecutor();
        //2.线程数可变的线程池   理论最大线程数为Integer.MAX_VALUE
            //如果有线程空闲  那么优先使用空闲线程执行新任务   如果没有空闲线程才创建新线程执行新任务
        Executors.newCachedThreadPool();
        //3.固定数量的线程池   线程数量固定  处理不过来只能放到任务队列里
        Executors.newFixedThreadPool(10);
        //4.线程池大小为1的可定时调度的任务线程池
        Executors.newSingleThreadScheduledExecutor();
        //5.可指定核心线程数的可定是调度的任务线程池
        Executors.newScheduledThreadPool(10);

        //短信验证码服务
        AsyncSmsVerificationCodePusher asyncSmsVerificationCodePusher = new AsyncSmsVerificationCodePusher();
        //组装验证码发送task  模拟三次
        SmsVerificationCodeTask smsVerificationCodeTask1 = new SmsVerificationCodeTask(18928785679L);
        SmsVerificationCodeTask smsVerificationCodeTask2 = new SmsVerificationCodeTask(18928785678L);
        SmsVerificationCodeTask smsVerificationCodeTask3 = new SmsVerificationCodeTask(18928785677L);

        asyncSmsVerificationCodePusher.sendSmsVerificationCode(smsVerificationCodeTask1);
        asyncSmsVerificationCodePusher.sendSmsVerificationCode(smsVerificationCodeTask2);
        asyncSmsVerificationCodePusher.sendSmsVerificationCode(smsVerificationCodeTask3);

        try {
            System.out.println("开始休眠");
            Thread.sleep(1000);
            System.out.println("结束休眠");
        } catch (Exception e) {

        }
    }
}
