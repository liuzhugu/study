package org.liuzhugu.javastudy.course.ruyuanconcurrent.thread;

public class Account {
    public void draw(double drawAmount) {
        synchronized (this) {
            System.out.println("hello");
        }
    }
    public synchronized void drawTask() {
        System.out.println("hello");
    }
}
