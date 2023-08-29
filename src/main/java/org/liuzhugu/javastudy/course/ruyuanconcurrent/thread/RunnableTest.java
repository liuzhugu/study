package org.liuzhugu.javastudy.course.ruyuanconcurrent.thread;

public class RunnableTest implements Runnable {
    @Override
    public void run() {
        for (int i = 1;i < 11;i++) {
            System.out.println(Thread.currentThread().getName() + " thread " + i);
        }
    }
    public static void main(String[] args) {
        Thread t1 = new Thread(new RunnableTest(),"thread1");
        Thread t2 = new Thread(new RunnableTest(),"thread2");
        Thread t3 = new Thread(new RunnableTest(),"thread3");
        t1.start();
        t2.start();
        t3.start();
    }
}
