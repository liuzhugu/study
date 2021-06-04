package org.liuzhugu.javastudy.course.concurrent;

public class TestCount {
    private long count = 0;
    private void add10K(){
        int index = 0;
        while (index ++ < 10000) {
            count = count + 1;
        }
    }
    public static void main(String[] args) throws Exception{
        TestCount test = new TestCount();
        Thread t1 = new Thread(()->{
            test.add10K();
        });
        Thread t2 = new Thread(()->{
           test.add10K();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(test.count);
    }
}
