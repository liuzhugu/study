package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class SnakeYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 7) {
            System.out.println("今年是蛇年");
        }
    }
}
