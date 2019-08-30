package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class HorseYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 10) {
            System.out.println("今年是马年");
        }
    }
}
