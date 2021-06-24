package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 异步执行提高效率
 * 结果准备好才能获取  保证程序正确
 * */
public class Promise {
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();

        //1.通过Promisor的create获取Promise
        Future<Object> promise = Promisor.create(start);

        //2.通过主线程完成准备茶叶茶杯任务  从而实现并行
        System.out.println("准备茶叶茶杯，需要3分钟，当前用时: " +
                (System.currentTimeMillis() - start) + " ms");
        NoPromise.TeaAndCup teaAndCup = new NoPromise.TeaAndCup();
        Thread.sleep(3000);
        teaAndCup.setStatus(true);
        System.out.println("茶杯茶叶结束，总共用时" + (System.currentTimeMillis() - start) + " ms");

        //3.通过Promise获取任务结果

        //第一次用Promise  查看任务是否结束
        if (! promise.isDone()) {
            System.out.println("茶杯茶叶准备结束，等待烧水完成");
        }

        //第二次用Promise  获取Result  可能阻塞
        NoPromise.BoilWater boilWater = (NoPromise.BoilWater) promise.get();
        System.out.println("获取到烧水完成信号，当前用时:" + (System.currentTimeMillis() - start) + " ms");

        System.out.println("准备工作结束，开始泡茶makeTea！");
        System.out.println("总共用时: " + (System.currentTimeMillis() - start) + " ms");
    }

    //一方面并行执行   节省了时间
    //另一方面   结果准备好才能获取  避免了错误
    static class Promisor {
        public static Future<Object> create(long startTime) {
            //1.定义任务
            FutureTask<Object> futureTask = new FutureTask<Object>(() -> {
               System.out.println("开始烧水，当前用时:" + (System.currentTimeMillis() - startTime) + " ms");
                NoPromise.BoilWater boilWater = new NoPromise.BoilWater();
                Thread.sleep(15000);
                return boilWater;
            });
            //2.开始异步执行任务
            new Thread(futureTask).start();
            //3.立即返回Promise
            return futureTask;
        }
    }
}
