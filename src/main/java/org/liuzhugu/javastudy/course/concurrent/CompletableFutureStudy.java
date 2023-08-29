package org.liuzhugu.javastudy.course.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureStudy {
    public static void main(String[] args) {

//        //任务1:洗水壶 -> 烧开水
//        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() ->{
//           System.out.println("T1:洗水壶..");
//           sleep(1,TimeUnit.SECONDS);
//
//            System.out.println("T1:烧开水..");
//            sleep(15,TimeUnit.SECONDS);
//        });
//
//        //任务2:洗茶壶 -> 洗茶杯 -> 拿茶叶
//        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() ->{
//            System.out.println("T2:洗茶壶..");
//            sleep(1,TimeUnit.SECONDS);
//
//            System.out.println("T2:洗茶杯..");
//            sleep(2,TimeUnit.SECONDS);
//
//            System.out.println("T2:拿茶叶..");
//            sleep(1,TimeUnit.SECONDS);
//
//            return "龙井";
//        });
//
//        //任务1和任务2完成之后才能开始任务3:泡茶
//        CompletableFuture<String> f3 = f1.thenCombine(f2,(__,tf) -> {
//            System.out.println("T1:拿到茶叶:" + tf);
//            System.out.println("T1:泡茶...");
//            return "上茶:" + tf;
//        });
//
//        //等待任务3执行结果
//        System.out.println(f3.join());

//        CompletableFuture<String> f0 = CompletableFuture.
//                supplyAsync(() -> {return "Hello World!";})
//                .thenApply(s -> s + " QQ")
//                .thenApply(String::toUpperCase);
//        System.out.println(f0.join());

        CompletableFuture<Integer> f0 = CompletableFuture
                .supplyAsync(() -> (7/0))
                .thenApply(r -> r * 10)
                .exceptionally(e -> 0);
        System.out.println(f0.join());

    }
    //休眠
    static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        }catch (InterruptedException e) {}
    }
}
