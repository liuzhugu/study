package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 报警中心
 * */
@Slf4j
public class AlarmAgent {

    private static volatile boolean connectedToServer = false;

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

    //保护条件
    private final Predicate agentConnected = new Predicate() {
        @Override
        public boolean evaluate() {
            //连接是否建立完成
            return connectedToServer;
        }
    };

    //阻塞器
    private final Blocker blocker = new ConditionVarBlocker();



    /**
     * 上传报警信息给报警服务
     * */
    public void sendAlarm(AlarmInfo alarmInfo) throws Exception{
        //构建guardedAction
        GuardedAction<Void> guardedAction = new GuardedAction(agentConnected) {
            public Void call() throws Exception {
                doSendAlarm(alarmInfo);
                return null;
            }
        };

        blocker.callWithGuard(guardedAction);
    }

    /**
     * 通过网络连接  将告警信息发给告警服务器
     * */
    private void doSendAlarm(AlarmInfo alarmInfo) {
        log.info("start send alarm:" + alarmInfo);
        //模拟发送告警信息至服务器的耗时
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("end send alarm");

    }



    /**
     * 确认和报警服务器建立连接
     * */
    private void onConnected() {
        //通过blocker去唤醒
        try {
            blocker.signalAfter(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    //唤醒前的动作状态
                    //修改连接报警服务器的状态
                    connectedToServer = true;
                    log.info("connect to server");
                    //条件满足  去唤醒
                    return Boolean.TRUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 和报警中心断开连接
     * */
    protected void onDisConnected() {
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
     private class ConnectingTask implements Runnable {
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
            onConnected();
        }
    }

    /**
     * 心跳检查线程   定时检查与告警服务器的连接是否正常
     * 发现连接异常后自动重新连接
     * */
    private class HeartbeatTask extends TimerTask {
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

