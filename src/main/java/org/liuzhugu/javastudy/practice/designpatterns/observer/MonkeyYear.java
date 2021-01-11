package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class MonkeyYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 1) {
            System.out.println("今年是猴年");
        }
    }
}
