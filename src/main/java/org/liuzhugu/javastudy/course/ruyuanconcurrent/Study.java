package org.liuzhugu.javastudy.course.ruyuanconcurrent;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable.SmsRouter;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer.HouseContractAttachmentProcessor;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer.ProduceConsumer;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.CloudClient;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem.SecuritySystem;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.ClientHandler;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.ConcreteTerminatableThread;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.RuYuanClient;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.TerminationToken;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify.GuardedQueue;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ExecutorService_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Runnable_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ThreadPoolExecutor_;

import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Study {
    public static void main(String[] args) {
        //1.不可变类模式
        //  不可变就没有并发冲突
        SmsRouter smsRouter = new SmsRouter();
        //2.保护性暂挂模式
        //  条件不满足时挂起线程  等待条件满足时被唤醒
        GuardedQueue guardedQueue = new GuardedQueue();
        SecuritySystem securitySystem = new SecuritySystem();
        //3.二阶段终止模式
        //  相比于直接暴力终止正在运行的线程  通知线程结束  然后线程结束完正在运行的任务以后安全退出更好
        ConcreteTerminatableThread clientHandler = new ConcreteTerminatableThread(new TerminationToken(false));
        //4.Promise模式
        //  希望通过异步提高执行效率  但又需要异步执行的结果时
        CloudClient cloudClient = new CloudClient();
        //5.生产者-消费者
        //  解耦生产者和消费者  异步   缓冲
        ProduceConsumer produceConsumer = new ProduceConsumer();
        HouseContractAttachmentProcessor processor = new HouseContractAttachmentProcessor();
        //  线程池
        ThreadPoolExecutor_ executor = new ThreadPoolExecutor_(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable_>(),null,null);
        executor.runWorker(null);
    }
}
