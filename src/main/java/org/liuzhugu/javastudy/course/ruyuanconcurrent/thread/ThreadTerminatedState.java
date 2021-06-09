package org.liuzhugu.javastudy.course.ruyuanconcurrent.thread;

public class ThreadTerminatedState implements Runnable {

    public static void main(String[] args) {
        Thread thread = new Thread(new ThreadTerminatedState());
        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(thread.getState());
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(i);
        }
    }
}
