package org.liuzhugu.javastudy.book.logicjava.concurrentbasic;

/**
 * 并发操作共享内存
 * */
public class ShareMemoryDemo implements Runnable{
    private static int num = 0;

    @Override
    public void run() {
        for (int i = 0;i < 10000;i ++) {
            num = num + 1;
        }
    }
    public static void main(String[] args) throws Exception{
        Thread first = new Thread(new ShareMemoryDemo());
        Thread second = new Thread(new ShareMemoryDemo());
        first.start();
        second.start();
        first.join();
        second.join();
        System.out.println(num);
    }
}
