package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

import java.util.List;

/**
 * 终端线程的抽象类
 * */
public abstract class AbstractTerminationThread extends Thread implements Termination {

    public final TerminationToken terminationToken;

    private List<ClientHandler> waitStopClientHandlers;

    public AbstractTerminationThread(TerminationToken terminationToken) {
        this.terminationToken = terminationToken;
    }

    /**
     * 执行模板
     *  要想使用  必须实现真正的业务逻辑   但中断线程的逻辑却在这里就实现了
     *  想使用中断逻辑的   只需要继承一下该类  然后实现一下业务逻辑   就可以将业务逻辑加上中断线程的实现
     * */
    @Override
    public void run() {
        Exception ex = null;
        try {
            for (;;) {
                //死循环  先判断中断实例的标识是否为true 并且有没有未完成的任务
                System.out.println("告警线程执行，此时中断标志位: " + terminationToken);
                if (terminationToken.isToShutdown() && terminationToken.reserva) {
                    //线程已经终止   中断线程退出
                    System.out.println("中断标志位true，未完成任务为0，告警线程退出");
                    break;
                }

                //执行具体的业务逻辑
                doRun();
            }
        } finally {
            try {
                System.out.println("告警线程停止，回调终止后的清理工作");
                doClean(ex);
            } finally {
                //通知terminationToken管理的所有线程实例进行退出
                System.out.println("标志实例对象中一个线程终止，通知其他线程终止");
                terminationToken.notifyThreadTermination(this);
            }
        }
    }

    /**
     * 真正的业务逻辑
     * */
    public abstract void doRun();

    /**
     * 清理工作
     * */
    public abstract void doClean(Exception e);


    /**
     * 中断方法
     * */
    @Override
    public void doTermination() {
        System.out.println("doTermination -> 准备打烊，不接待客户，大家都把手上的工作处理完放下把");
        this.interrupt();
        waitStopClientHandlers.forEach(ClientHandler::stop);
    }
}
