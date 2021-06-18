package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcreteTerminatableThread extends AbstractTerminationThread {

    /**
     * 顾客登记铺名单
     * */
    private ConcurrentLinkedQueue<ClientHandler> clientQueue = new ConcurrentLinkedQueue<>();

    /**
     * 自主就餐区 + 排队等待区
     * */
    private ExecutorService executor = Executors.newFixedThreadPool(10);


    public ConcreteTerminatableThread(TerminationToken terminationToken) {
        super(terminationToken);
    }

    @Override
    public void doRun() {

    }

    @Override
    public void doClean(Exception e) {
        System.out.println("打扫卫生，熄灯");
        executor.shutdown();
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
