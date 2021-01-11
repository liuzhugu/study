package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class PigYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 3) {
            System.out.println("今年是猪年");
        }
    }
}
