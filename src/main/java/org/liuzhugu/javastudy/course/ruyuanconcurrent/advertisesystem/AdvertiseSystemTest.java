package org.liuzhugu.javastudy.course.ruyuanconcurrent.advertisesystem;


import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AlarmType;

/**
 * 广告计费智能预警系统   广告计费的时候 如果发现广告主钱快没了 就智能预警通知他
 * */
public class AdvertiseSystemTest {
    //告警日志抑制阈值
    private static final int ALARM_MSG_SUPRESS_THRESHOLD = 10;

    static {
        //初始化告警模块
        AlarmManager.getInstance().init();
    }

    public static void main(String[] args) {
        AdvertiseSystemTest advertiseSystemTest = new AdvertiseSystemTest();
        String advertiser = "liuzhugu";
        String phoneNumber = "18928785671";
        try {
            /**
             * 投放广告
             * */
            advertiseSystemTest.putAdvertiser(advertiser,phoneNumber);
        } catch (Exception e) {
            //当前广告主钱快没了预警
            final AlarmManager alarmManager = AlarmManager.getInstance();
            //告警被重复发送至报警模块的次数
            int duplicateSubmissionCount;
            String alarmId = "00000010000020";

            //上报预警系统到智能预警系统
            duplicateSubmissionCount = alarmManager.sendAlarm(AlarmType.FAULT,
                    alarmId,
                    advertiser,
                    phoneNumber);
            if (duplicateSubmissionCount < ALARM_MSG_SUPRESS_THRESHOLD) {
                //超过一定的告警次数后打印日志  不再上报到只能预警系统
                System.out.println("Alarm[" + alarmId + "] advertiser:"
                        + advertiser + "，phoneNumber:" + phoneNumber);
            } else {
                if (duplicateSubmissionCount == ALARM_MSG_SUPRESS_THRESHOLD) {
                    System.out.println("Alarm[" + alarmId + "] was raised more than "
                            + ALARM_MSG_SUPRESS_THRESHOLD
                            + " times, it will no longer be logged.");
                }
            }
        }
    }

    //投放广告
    private void putAdvertiser(String advertiser,String phoneNumber) throws Exception {
        //根据广告内容  计算广告费用  低于阈值时报警
        throw new RuntimeException(advertiser + "号主，XXX广告系统快没钱了，请及时充值");
    }
}
