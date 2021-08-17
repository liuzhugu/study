package org.liuzhugu.javastudy.course.ruyuanconcurrent.advertisesystem;


import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AbstractTerminationThread;
import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AlarmType;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:上报广告计费系统预警信息的工作线程
 * 相当于半同步、半异步中的同步层
 * */
public class AlarmSendingThread extends AbstractTerminationThread {
    /**
     * 广告费告警队列
     * */
    private final BlockingQueue<AlarmInfo> alarmQueue;

    /**
     * 已经提交的广告费告警信息
     * */
    private final ConcurrentHashMap<String, AtomicInteger> submittedAlarmRegistry;

    public AlarmSendingThread() {
        //广告费告警队列
        this.alarmQueue = new ArrayBlockingQueue<>(100);
        this.submittedAlarmRegistry = new ConcurrentHashMap<>();
    }

    /**
     * 发送广告费告警信息
     *
     * @param  alarmInfo 广告费告警信息
     * @return 重复提交当前告警信息的数量
     * */
    public int sendAlarm(AlarmInfo alarmInfo) {
         if (terminationToken.isToShutdowm()) {
             //已终止
             System.out.println("reject alarm:" + alarmInfo);
             return -1;
         }
         try {
             //放到广告费告警队列中
             AtomicInteger preSubmittedCounter;
             preSubmittedCounter = submittedAlarmRegistry.putIfAbsent(alarmInfo.getId(),new AtomicInteger());
             if (preSubmittedCounter == null) {
                 //代表之前该类型的广告费告警为空
                 //未完成的任务 + 1
                 terminationToken.reservations.incrementAndGet();
                 //放入到广告费告警队列中
                 alarmQueue.put(alarmInfo);
             } else {
                 //当前的故障还没有恢复  不需要重复上报广告费告警  只是增加广告费告警数量的次数
                 return preSubmittedCounter.incrementAndGet();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         return 0;
    }

    @Override
    protected void doRun() throws InterruptedException {
        //执行业务逻辑   从广告费告警队列中拉取广告费告警信息  上报到监控系统中
        AlarmInfo alarmInfo;
        alarmInfo = alarmQueue.take();
        System.out.println("告警线程从队列中拉到告警信息:" + alarmInfo);

        //广告费告警任务数量 -1
        terminationToken.reservations.decrementAndGet();

        //发送广告费报警信息到智慧监控系统中
        try {
            doSendAlarm();
            System.out.println("广告费告警信息:" + alarmInfo + " 上报完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果当前告警类型是恢复告警  则需要清空当前告警的统计信息重置为空
        if (Objects.equals(alarmInfo.getAlarmType(),AlarmType.RESUME)) {
            System.out.println("广告费告警:" + alarmInfo + "已恢复，清空告警次数");
            submittedAlarmRegistry.remove(alarmInfo.getUniqueIdByAlarmType(AlarmType.FAULT));
            submittedAlarmRegistry.remove(alarmInfo.getUniqueIdByAlarmType(AlarmType.RESUME));
        }
    }

    /**
     * 上报广告费告警信息到智慧监控系统中
     * */
    private void doSendAlarm() {
        //这里通过socket的方式上报到智慧监控系统中
        //模拟调用
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
