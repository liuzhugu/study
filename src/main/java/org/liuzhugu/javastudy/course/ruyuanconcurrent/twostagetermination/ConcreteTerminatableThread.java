package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ExecutorService_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Executors_;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ConcreteTerminatableThread extends AbstractTerminationThread {

    /**
     * 顾客登记铺名单
     * */
    private ConcurrentLinkedQueue<ClientHandler> clientQueue = new ConcurrentLinkedQueue<>();

    /**
     * 自主就餐区 + 排队等待区
     * */
    private ExecutorService_ executor = Executors_.newFixedThreadPool(10);


    public ConcreteTerminatableThread(TerminationToken terminationToken) {
        super(terminationToken);
    }

    @Override
    public void doRun() {

    }

    @Override
    public void doClean(Exception e) {
        System.out.println("打扫卫生，熄灯");
        //线程池、阻塞队列里的任务统统不允许再执行了  不过会返回阻塞队列中没执行完的任务
        executor.shutdownNow();
    }

    @Override
    public void doTerminate() {
        //客户端处理器  用于对socket的包装
        ClientHandler clientHandler;
        while ((clientHandler = clientQueue.poll()) != null) {
            //结束所有client
            clientHandler.stop();;
        }
    }
}
