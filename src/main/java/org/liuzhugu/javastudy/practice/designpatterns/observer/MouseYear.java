package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class MouseYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 5) {
            System.out.println("今年是鼠年");
        }
    }
}
