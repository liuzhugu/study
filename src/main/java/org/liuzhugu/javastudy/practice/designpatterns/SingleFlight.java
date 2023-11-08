package org.liuzhugu.javastudy.practice.designpatterns;

import ch.qos.logback.core.encoder.ByteArrayUtil;

import java.util.concurrent.CountDownLatch;

/**
 * 在源头减少没必要的并发竞争 如都是要干同一件事得到相同的结果  那么选出代表去执行
 * 其他线程等待结果就行  其他线程注意控制等待的超时时间   看看是死等还是超时放弃等待
 * 放弃之后可以自己去干
 * */
public class SingleFlight {

    public static void  main(String[] args) {
        //每条线程代表发现热点缓存未命中  都打算去查库然后设置缓存
        CallManage callManage = new CallManage();
        int count = 10;
        CountDownLatch cld = new CountDownLatch(count);
        for (int i = 0;i < count;i ++) {
            new Thread(() -> {
                try {
                    cld.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] value = callManage.run("key",() ->
                        //回库操作    只执行一次
                    {
                    //回库读取数据
                    System.out.println("回库读取数据");
                    //返回数据结果
                    return ByteArrayUtil.hexStringToByteArray("数据结果");
                });
                System.out.println(ByteArrayUtil.toHexString(value));
            }).start();
            cld.countDown();
        }
    }
}
