package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadpool;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.*;

import java.util.concurrent.*;


public class AsyncSmsVerificationCodePusher {
    private static final ExecutorService SMS_SEND_THREAD_POOL =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() + 1,
                    50,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(5000),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r,"sms_send_thread_pool");
                            thread.setDaemon(true);
                            return thread;
                        }
                    },
                    //空实现  默认不做任何处理
                    new ThreadPoolExecutor.DiscardPolicy()
            );

    public void sendSmsVerificationCode(SmsVerificationCodeTask task) {
        SMS_SEND_THREAD_POOL.submit(task);
    }
}
