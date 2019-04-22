package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class SheepYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 11) {
            System.out.println("今年是羊年");
        }
    }
}
