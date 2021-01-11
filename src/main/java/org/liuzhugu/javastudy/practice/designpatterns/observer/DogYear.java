package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class DogYear implements ObserverTime {
    @Override
    public void time(int time) {
        if (time == 9) {
            System.out.println("今年是狗年");
        }
    }
}
