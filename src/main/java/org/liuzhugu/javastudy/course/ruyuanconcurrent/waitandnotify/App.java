package org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) throws Exception{
        GuardedQueue guardedQueue = new GuardedQueue();

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.execute(() -> {
            guardedQueue.get();
        });

        Thread.sleep(2000);

        executorService.execute(() -> {
            guardedQueue.put(20);
        });

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }
}
