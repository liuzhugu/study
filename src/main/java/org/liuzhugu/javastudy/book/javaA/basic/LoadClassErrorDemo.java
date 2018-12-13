package org.liuzhugu.javastudy.book.javaA.basic;

/**
 * Created by liuting6 on 2017/12/26.
 * 代码不当导致class加载失败
 */
abstract  class A{
    public A(){
        list();
    }
    public void list(){
        test();
    }
    abstract void test();
}
class B extends A{
    private final static B instance =new B();
    public static B getInstance(){return instance;}
    public void test(){
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        instance.test2();
    }
    public void test2(){}
}
public class LoadClassErrorDemo {
    public static void main(String[] args){
        new Thread(){
            public void run(){
                try{
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                B.getInstance().test2();
            }
        }.start();
        new Thread(){
            public void run(){
                try{
                    Thread.sleep(400);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                B.getInstance().test2();
            }
        }.start();
    }
}
