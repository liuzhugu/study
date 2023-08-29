package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

import java.util.concurrent.ArrayBlockingQueue;

public class SecuritySystem {
    public static void main(String[] args) {
        AlarmAgent alarmAgent = new AlarmAgent();
        alarmAgent.init();

    }
}
