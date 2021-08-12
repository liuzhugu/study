package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;


import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.Termination;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.TerminationToken;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 终端线程的抽象类
 * */
public abstract class AbstractTerminationThread extends Thread implements Termination {

    public final TerminationToken terminationToken;

    private String threadName;

    public static AtomicInteger threadCount = new AtomicInteger(0);

    public AbstractTerminationThread(String threadName) {
        this(threadName,new TerminationToken());
    }

    public AbstractTerminationThread(String threadName,TerminationToken terminationToken) {
        this.terminationToken = terminationToken;
        this.threadName = threadName;
        System.out.println("注册线程到线程停止的标志实现对象队列中");
        terminationToken.register(this);
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
            //死循环  不断执行任务
            for (;;) {
                //死循环  先判断中断实例的标识是否为true 并且有没有未完成的任务
                if (terminationToken.isToShutdowm() && terminationToken.reservations.get() <= 0) {
                    //线程已经终止   中断线程退出
                    System.out.println("中断标志位true，未完成任务为0，线程 " + threadName + " 退出");
                    break;
                }

                //执行具体的业务逻辑
                doRun();
            }
        } catch (Exception e) {
            //中断线程可能调用interrupt被中断
            ex = e;
            if (e instanceof InterruptedException) {
                //中断线程相应退出
                System.out.println("中断响应:" + e);
            }
        }
        //当上面循环跳出后  也就是线程终止了  此时执行线程停止的一些清理工作
        finally {
            try {
                System.out.println("线程停止，回调终止后的清理工作");
                doCleanup(ex);
            } finally {
                //通知terminationToken管理的所有线程实例进行退出
                System.out.println("标志实例对象中一个线程终止，通知其他线程终止");
                //标记类注册了所有工作者线程  当一个线程终止后   它可以通知其他线程进行终止
                terminationToken.notifyThreadTermination(this);
            }
        }
    }

    public void terminate() {
        //设置标志实例对象为true
        System.out.println("设置中断标志对象为中断状态");
        this.terminationToken.setToShutdowm(true);

        try {
            doTerminate();
        } finally {
            //如果没有等待任务  则强制去停止任务
            if (terminationToken.reservations.get() <= 0) {
                super.interrupt();
            }
        }
    }

    /**
     * 留给子类去实现具体的线程业务逻辑
     */
    protected abstract void doRun() throws Exception;


    /**
     * 留给子类去实现，完成线程终止后的一些清理动作
     *
     * @param ex
     */
    protected  void doCleanup(Exception ex) {}


    /**
     * 执行终止线程的逻辑 留个子类去具体的实现
     */
    protected void doTerminate() {}
}
