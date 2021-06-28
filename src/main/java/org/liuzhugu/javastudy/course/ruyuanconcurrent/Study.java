package org.liuzhugu.javastudy.course.ruyuanconcurrent;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable.SmsRouter;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.CloudClient;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem.SecuritySystem;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.ClientHandler;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.ConcreteTerminatableThread;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.RuYuanClient;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.TerminationToken;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify.GuardedQueue;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;

import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Study {
    public static void main(String[] args) {
        //1.不可变类模式   不可变就没有并发冲突
        SmsRouter smsRouter = new SmsRouter();
        //2.保护性暂挂模式
        GuardedQueue guardedQueue = new GuardedQueue();
        SecuritySystem securitySystem = new SecuritySystem();
        //3.二阶段终止模式
        ConcreteTerminatableThread clientHandler = new ConcreteTerminatableThread(new TerminationToken(false));
        //4.Promise模式
        CloudClient cloudClient = new CloudClient();
        //5.生产者-消费者

    }
}
