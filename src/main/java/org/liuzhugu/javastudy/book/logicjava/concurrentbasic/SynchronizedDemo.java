package org.liuzhugu.javastudy.book.logicjava.concurrentbasic;

/**
 * synchronized
 * */
public class SynchronizedDemo {
    //修饰静态方法
    public synchronized static void staticMethod(String name) {
        System.out.println("thread " + name + " call staticMethod start");
        try {
            Thread.sleep(1000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("thread " + name + " call staticMethod end");
    }
    //修饰普通方法
    public synchronized void normalMethod(String name) {
        System.out.println("thread " + name + " call normalMethod start");
        try {
            Thread.sleep(1000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("thread " + name + " call normalMethod end");
    }

    public static void main(String[] args) {
        //测试调静态方法,first执行完之后second才能执行
//        Thread first = new Thread(new TestThread("first"));
//        Thread second = new Thread(new TestThread("second"));
//        first.start();
//        second.start();

    }

}

class TestThread implements Runnable{
    private String name;
    public TestThread(String name){
        this.name = name;
    }
    @Override
    public void run() {
        //访问静态方法
        //SynchronizedDemo.staticMethod(name);
        //
    }
}