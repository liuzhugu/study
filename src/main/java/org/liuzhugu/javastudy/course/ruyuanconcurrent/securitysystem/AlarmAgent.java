package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

import org.checkerframework.checker.units.qual.C;
import org.liuzhugu.javastudy.book.worldviewinthecode.Factory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 报警中心
 * */
public class AlarmAgent {

    private static volatile boolean connectedToServer = true;

    /**
     * 初始化  报警服务
     * 1.连接报警服务器的线程去进行连接
     * 2.定时调度线程每隔5s去检查一次连接
     * */
    public void init() {
        //报警服务与报警服务器连接的线程
        Thread connectingThread = new Thread(new ConnectingTask());
        connectingThread.start();

        //每个5s发送一次心跳到报警服务器
        ScheduledThreadPoolExecutor heartbeatExecutor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread();

                thread.setName("heartbeat-thread-" + index);
                //当jvm退出时退出
                thread.setDaemon(true);
                return thread;
            }
        });

        //定时器,每5s执行一次
        heartbeatExecutor.scheduleAtFixedRate(new HeartbeatTask(),5000,2000, TimeUnit.MILLISECONDS);
    }

    public void sendAlarm(AlarmInfo alarmInfo) {

    }

    private void doSendAlarm(AlarmInfo alarmInfo) {

    }

    /**
     * 确认和报警服务器建立连接
     * */
    private void doConnected() {
        //通过blocker去唤醒

    }

    /**
     * 和报警中心断开连接
     * */
    private void onDisConnected() {
        //通过volatile的语义让其他线程能及时读取到
        //其他线程上报报警信息是stateOperation不满足则阻塞
        connectedToServer = false;
    }

    /**
     * 重新连接
     * */
    private void reConnected() {
        //重新执行一次
        ConnectingTask connectingTask = new ConnectingTask();
        //直接通过心跳线程执行一次重连,这里就不单独开启thread
        connectingTask.run();
    }

    /**
     * 与报警服务器建立连接的线程
     * */
     class ConnectingTask implements Runnable {
        @Override
        public void run() {
            //走socketChannel的方式和报警服务器建立一个连接
            //这里我们简单模拟一下  10s后建立连接
            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //连接建立完成
            System.out.println("alarm connected");
            doConnected();
        }
    }

    /**
     * 心跳检查线程
     * */
    class HeartbeatTask implements Runnable {
        @Override
        public void run() {
            //通过socket和报警服务器发送心跳机制
            if (! testConnection()) {
                //连接断开
                onDisConnected();
                //重连
                reConnected();
            }
        }
    }

    /**
     * 测试连接是否正常
     * */
    private boolean testConnection() {
        //通过socket给报警服务器发送一次连接
        //模拟发送一次
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("test connection normal");
        return true;
    }
}

