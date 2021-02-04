package org.liuzhugu.javastudy.course.commonfault.concurrent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Interesting {
    volatile int a = 0;
    volatile int b = 0;

    //没加synchronized的话  add的两个操作  和a < b之间是乱序的 会出现各种可能
    //但加了后add和compare同一时间只有一个能执行  顺序确定

    public synchronized void add() {
        for (int i = 0;i < 100000;i ++) {
            a ++;
            b ++;
        }
    }
    public synchronized void compare() {
        for (int i = 0;i < 100000;i ++) {
            //a < b操作分为三步  取a  取b  比较   并非原子性
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
            }
        }
    }
}
