package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;


public class DeadLock2 {
    public static void main(String[] args) {
        Account a = new Account();
        Account b = new Account();
        Thread first = new Thread(new Execute(a,b,100));
        Thread second = new Thread(new Execute(b,a,200));
        first.start();
        second.start();
    }

    private static class SingleTonHolder{
        private static Allocator INSTANCE = new Allocator();
    }

    public static Allocator getInstance(){
        return SingleTonHolder.INSTANCE;
    }
}
