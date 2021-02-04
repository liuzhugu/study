package org.liuzhugu.javastudy.course.commonfault.concurrent;

public class Test {
    public static void main(String[] args) {
        Interesting interesting = new Interesting();
        new Thread(() -> interesting.add()).start();
        new Thread(() -> interesting.compare()).start();
    }
}
