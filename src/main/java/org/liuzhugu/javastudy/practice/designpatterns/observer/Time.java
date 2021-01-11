package org.liuzhugu.javastudy.practice.designpatterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 报时
 * */
public class Time {
    //监控报钟的观察者
    private List<ObserverTime> observerTimes = new ArrayList<>();
    public void addObserver (ObserverTime observerTime) {
        observerTimes.add(observerTime);
    }
    public void run() {
        int time = 0;
       try {
           while(true) {
               Thread.sleep(1000);
               time = (time + 1) % 12;
               //通知所有观测者,观测者增减并不会改变这个class的任何代码
               if(time == 0) {
                   System.out.println("又是一个轮回");
               }
               System.out.print("又过去了一年,");
               for (ObserverTime observerTime : observerTimes) {
                   observerTime.time(time);
               }
           }
       }catch (Exception e) {
           e.printStackTrace();
       }
    }

}
