package org.liuzhugu.javastudy.book.javaA.concurrency;

/**
 * Created by liuting6 on 2018/1/12.
 * 线程异常捕捉
 */
class TestExceptionHandle implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t,Throwable e){
        System.out.println("线程出现问题");
        e.printStackTrace();
    }
}
public class ExceptionHandleTest {
    public static void main(String[] args){
        Thread t=new Thread(){
            public void run(){
                Integer.parseInt("ABC");
            }
        };
        t.setUncaughtExceptionHandler(new TestExceptionHandle());
        t.start();
    }
}
