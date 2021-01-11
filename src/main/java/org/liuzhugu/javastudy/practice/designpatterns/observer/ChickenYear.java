package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class ChickenYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 4) {
            System.out.println("今年是鸡年");
        }
    }
}
