package org.liuzhugu.javastudy.course.concurrent;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FutureStudy {
    public static void main(String[] args) throws Exception {
//        //待执行任务
//        FutureTask futureTask = new FutureTask(() -> 1 + 2);
//
//        //交给线程池去执行
//        ExecutorService es = Executors.newCachedThreadPool();
//        es.submit(futureTask);
//        System.out.println(futureTask.get());
//
//        //交给一个单独的线程池去执行
//        Thread t = new Thread(futureTask);
//        t.start();
//        System.out.println(futureTask.get());

//        //创建线程池
//        ExecutorService es = Executors.newFixedThreadPool(3);
//
//        //异步获取报价
//        Future<Integer> f1 = es.submit(() -> getPriceByS1());
//        Future<Integer> f2 = es.submit(() -> getPriceByS2());
//        Future<Integer> f3 = es.submit(() -> getPriceByS3());

        //阻塞等待报价返回,但如果f1没返回,f2即使已经有返回了也无法处理
//        int price1 = f1.get();
//        es.submit(() -> save(price1));
//        int price2 = f2.get();
//        es.submit(() -> save(price2));
//        int price3 = f3.get();
//        es.submit(() -> save(price3));

        //改进,客户端硬编码返回顺序,给个队列异步塞,客户端依次从中取,谁先到处理谁,将变化抽离出去
//        BlockingQueue<Integer> bq = new LinkedBlockingDeque<>();
//        es.submit(() -> {
//            try {
//                bq.put(f1.get());
//            }catch (Exception e){}
//        });
//        es.submit(() -> {
//            try {
//                bq.put(f2.get());
//            }catch (Exception e){}
//        });
//        es.submit(() -> {
//            try {
//                bq.put(f3.get());
//            }catch (Exception e){}
//        });
//        for(int i = 0;i < 3;i ++) {
//            Integer price =bq.take();
//            es.submit(() -> save(price));
//        }

        //OR  只要有一个完成都结束
        ExecutorService es = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<Integer> cs = new ExecutorCompletionService<>(es);
        //保存future
        List<Future<Integer>> futures = new ArrayList<>();
        futures.add(cs.submit(() -> getPriceByS1()));
        futures.add(cs.submit(() -> getPriceByS2()));
        futures.add(cs.submit(() -> getPriceByS3()));
        //获取future处理结果
        for (int i = 0;i < 3;i ++) {
            try {
                Integer result = cs.take().get();
                if (result != null) {
                    break;
                }
            }
            //有完成的或都未完成,取消全部任务
            finally {
                for (Future<Integer> future : futures) {
                    future.cancel(true);
                }
            }
        }
        //如果没有完成的或是都未完成  取消所有future
    }

    private static int getPriceByS1() {
        Random random = new Random();
        try {
            int time = random.nextInt(10);
            Thread.sleep(time);
            System.out.println("1 sleep " + time + "s");
        }catch (Exception e){}
        return 1;
    }
    private static int getPriceByS2() {
        Random random = new Random();
        try {
            int time = random.nextInt(10);
            Thread.sleep(time);
            System.out.println("2 sleep " + time + "s");
        }catch (Exception e){}
        return 2;
    }
    private static int getPriceByS3() {
        Random random = new Random();
        try {
            int time = random.nextInt(10);
            Thread.sleep(time);
            System.out.println("3 sleep " + time + "s");
        }catch (Exception e){}
        return 3;
    }
    private static void save(int price) {System.out.println("save price:" + price);}
}

