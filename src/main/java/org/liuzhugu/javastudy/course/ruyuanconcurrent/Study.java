package org.liuzhugu.javastudy.course.ruyuanconcurrent;


import org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable.SmsRouter;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork.ArticleAccessTest;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer.HouseContractAttachmentProcessor;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer.ProduceConsumer;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.CloudClient;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.CloudSyncTaskTest;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem.SecuritySystem;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.threadlocal.UserPasswordSystemManager;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.threadpool.ExecutorsStudy;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AlarmManagerTest;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify.GuardedQueue;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Runnable_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ThreadPoolExecutor_;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Study {
    public static void main(String[] args) throws Exception {
        //1.不可变类模式
        //  不可变就没有并发冲突
        SmsRouter smsRouter = new SmsRouter();
        //2.保护性暂挂模式
        //  条件不满足时挂起线程  等待条件满足时被唤醒
        GuardedQueue guardedQueue = new GuardedQueue();
        SecuritySystem securitySystem = new SecuritySystem();
        //3.二阶段终止模式
        //  相比于直接暴力终止正在运行的线程  通知线程结束  然后线程结束完正在运行的任务以后安全退出更好
        AlarmManagerTest alarmTest = new AlarmManagerTest();
        //4.Promise模式
        //  希望通过异步提高执行效率  但又需要异步执行的结果时
        CloudClient cloudClient = new CloudClient();
        //5.生产者-消费者
        //  解耦生产者和消费者  异步   缓冲
        ProduceConsumer produceConsumer = new ProduceConsumer();
        HouseContractAttachmentProcessor processor = new HouseContractAttachmentProcessor();
        //6.线程池
            //创建参数
        ThreadPoolExecutor_ executor = new ThreadPoolExecutor_(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable_>(),null,null);
        executor.runWorker(null);
            //创建线程池方式
        ExecutorsStudy executorsStudy = new ExecutorsStudy();
        //7.ThreadLocal
        UserPasswordSystemManager userPasswordSystemManager = UserPasswordSystemManager.getInstance();


        //1.智慧制造监控系统
        AlarmManagerTest alarmManagerTest = new AlarmManagerTest();
        //2.个人网盘
        CloudSyncTaskTest cloudSyncTaskTest = new CloudSyncTaskTest();
        //3.长地址转换
        ArticleAccessTest articleAccessTest = new ArticleAccessTest();
    }
}
