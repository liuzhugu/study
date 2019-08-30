package org.liuzhugu.javastudy.practice.designpatterns.observer;

import org.springframework.stereotype.Component;

@Component
public class TigerYear implements ObserverTime{
    @Override
    public void time(int time) {
        if (time == 2) {
            System.out.println("今年是虎年");
        }
    }
}
