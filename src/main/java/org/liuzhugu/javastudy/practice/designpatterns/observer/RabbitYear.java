package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class RabbitYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 8) {
            System.out.println("今年是兔年");
        }
    }
}
