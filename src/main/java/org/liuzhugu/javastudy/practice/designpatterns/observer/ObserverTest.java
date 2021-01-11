package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class ObserverTest {
    public static void main(String[] args) {
        Time time = new Time();
        ClassPathXmlApplicationContext context = new
                ClassPathXmlApplicationContext("/designpatterns/observer/bean.xml");
        Map<String,ObserverTime> filterList = context.getBeansOfType(ObserverTime.class);
        if (filterList != null && filterList.size() != 0) {
            for (Map.Entry<String,ObserverTime> entry : filterList.entrySet()) {
                time.addObserver(entry.getValue());
            }
        }
        time.run();
    }
}
