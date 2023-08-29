package org.liuzhugu.javastudy.course.ruyuanconcurrent.forkjpinpool;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class ForkJoinRecursiveTaskTest {
    /**
     * 可进行处理的数字差，超过了就要进行任务分解
     */
    private  static int threshold = 100;
    public static void main(String[] args) {
        final ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> future = forkJoinPool.submit(new MyRecursiveTask(0, 1000));
        try {
            Integer result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private static class MyRecursiveTask extends RecursiveTask<Integer> {
        /**
         * 起始数字
         */
        private final int start;
        /**
         * 终止数字
         */
        private final int end;
        MyRecursiveTask(int start, int end) {
            this.start = start;
            this.end = end;
        }
        @Override
        protected Integer compute() {
            if (end - start <= threshold) {
                // 此范围的数字进行累加,并返回结果
                return IntStream.rangeClosed(start, end).sum();
            } else {
                // 将数据规模进行分解
                int middle = (start + end) / 2;

                // 将分解后的数据分发给子线程
                MyRecursiveTask firstTask = new MyRecursiveTask(start, middle);
                MyRecursiveTask secondTask = new MyRecursiveTask(middle + 1, end);

                // 将子任务，放入执行队列等待执行
                //secondTask.fork();
                //firstTask.fork();
                //将第一个任务同步执行  将第二个任务异步执行
                invokeAll(firstTask,secondTask);

                // 等待子线程任务执行完毕并获取聚合执行结果，
                return firstTask.join() + secondTask.join();
            }
        }
    }
}