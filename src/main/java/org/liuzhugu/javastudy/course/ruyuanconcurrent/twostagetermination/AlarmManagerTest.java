package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

/**
 * 智慧制造监控系统测试 场景：两阶段终止模式
 * */
public class AlarmManagerTest {
    public static void main(String[] args) {
        //告警管理组件
        AlarmManager alarmManager = AlarmManager.getInstance();
        //初始化告警管理组件  启动一个告警后台线程来上报请求到告警服务器中
        alarmManager.init();

        //发送告警任务
        new Thread(() -> {
            int duplicateAlarmNumber = alarmManager.sendAlarm(AlarmType.FAULT,"001","001告警信息");
            System.out.println("发送机器告警001完成，001重复提交次数:" + duplicateAlarmNumber);
        }).start();

        //发送告警任务
        new Thread(() -> {
            int duplicateAlarmNumber = alarmManager.sendAlarm(AlarmType.FAULT,"002","002告警信息");
            System.out.println("发送机器告警002完成，002重复提交次数:" + duplicateAlarmNumber);
        }).start();

        //发送告警任务
        new Thread(() -> {
            int duplicateAlarmNumber = alarmManager.sendAlarm(AlarmType.FAULT,"002","002告警信息");
            System.out.println("发送机器告警002完成，002重复提交次数:" + duplicateAlarmNumber);
        }).start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(alarmManager::shutDown).start();
    }
}
