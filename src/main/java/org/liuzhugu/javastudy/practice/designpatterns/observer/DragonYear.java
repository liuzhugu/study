package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class DragonYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 6) {
            System.out.println("今年是龙年");
        }
    }
}
