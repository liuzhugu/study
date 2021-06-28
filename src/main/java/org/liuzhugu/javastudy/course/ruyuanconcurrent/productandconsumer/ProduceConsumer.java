package org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer;

import org.checkerframework.checker.units.qual.A;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ExecutorService_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;

import java.util.concurrent.ArrayBlockingQueue;

public class ProduceConsumer {
    public static void main(String[] args) {
        //生产者线程池
        ExecutorService_ producerThreads = Executors_.newFixedThreadPool(3);
        //消费者线程池
        ExecutorService_ consumerThreads = Executors_.newFixedThreadPool(2);
        //任务队列  长度为10
        ArrayBlockingQueue<Task> taskQueue = new ArrayBlockingQueue<>(10);

        //生产者提交任务
        producerThreads.submit(() -> {
           try {
               taskQueue.put(new Task("任务"));
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        });
        //消费者处理任务
        consumerThreads.submit(() ->{
           try {
               Task task = taskQueue.take();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        });
    }

    static class Task {
        private String taskName;

        public Task(String taskName) {
            this.taskName = taskName;
        }
    }
}
