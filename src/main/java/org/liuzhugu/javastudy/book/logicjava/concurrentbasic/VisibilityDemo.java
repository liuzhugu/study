package org.liuzhugu.javastudy.book.logicjava.concurrentbasic;


/**
 * 可见性
 * */
public class VisibilityDemo implements Runnable{

    //第一条的话线程永远无法退出,因为主线程修改flag,线程却永远看不到
    //但改为volatile之后,主线程修改了立马更新主存,而线程每次读取flag时都会去主存取,这时就能看到flag的变更了
    //当然,只用同步也行,但在这个例子中只是为了维护可见性,因此用volatile就够了,性能更高一些
    //private static boolean shutDown = false;
    private static volatile boolean shutDown = false;

    private String name;
    public VisibilityDemo(String name) {
        this.name = name;
    }
    @Override
    public void run() {
        while(! shutDown) {
            //do nothing
        }
        System.out.println("thread " + name + " success exit!");
    }
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new VisibilityDemo("test thread"));
        t.start();
        Thread.sleep(1000);
        shutDown = true;
        System.out.println("main thread success exit!");
    }
}
