package org.liuzhugu.javastudy.course.concurrrent;

import java.util.concurrent.RecursiveTask;

public class ForkJoin {
    public static void main(String[] args) {

//        //斐波那契
//        //创建分治任务池
//        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
//        //创建分治任务
//        Fibonacci fibonacci = new Fibonacci(30);
//        //启动分治任务
//        Integer result = fibonacci.invoke();
//        System.out.println(result);

    }

    static class Fibonacci extends RecursiveTask<Integer> {
        final int n;
        public Fibonacci(int n) {
            this.n = n;
        }
        @Override
        protected Integer compute() {
            if (n <= 1)return 1;
            Fibonacci f1 = new Fibonacci(n - 1);
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            //等待子任务结果，并合并结果
            //f2在本线程执行,f1交由线程池其他线程处理,
            //如果调用两次fork和join的话,那么任务完全被分配出去,当前工作线程闲置了
            return f2.compute() + f1.join();
        }
    }
}
