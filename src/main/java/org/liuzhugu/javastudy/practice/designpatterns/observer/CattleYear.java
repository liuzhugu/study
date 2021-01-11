package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class CattleYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 0) {
            System.out.println("今年是牛年");
        }
    }
}
