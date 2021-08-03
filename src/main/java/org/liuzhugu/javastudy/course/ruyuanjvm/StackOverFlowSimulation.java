package org.liuzhugu.javastudy.course.ruyuanjvm;

public class StackOverFlowSimulation {
    public static long counter = 0;

    public static void main(String[] args) {
        work();
    }

    public static void work() {
        System.out.println("目前是第 " + (++counter) + "次调用方法");
        work();
    }
}
