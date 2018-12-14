package org.liuzhugu.javastudy.book.logicjava.classextends;

import org.liuzhugu.javastudy.book.logicjava.classexpansion.AbstractAdder;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class Base extends AbstractAdder{
//    public static String s="static_base";
//    public String m="base";
//    public  void staticTest(){
//        System.out.println("base static:"+s);
//    }
//    public void test(char c){
//        System.out.println("c");
//    }
//    public void test(long l){
//        System.out.println("l");
//    }
    public static int s;
    private int a;
    static {
        System.out.println("基类静态初始块，s: "+s);
        s=1;
    }
    {
        System.out.println("基类初始块,a: "+a);
        a=1;
    }
    public Base(){
        System.out.println("基类构造方法,a: "+a);
        a=2;
    }
    protected void step(){
        System.out.println("base s: "+s+",a: "+a);
    }
    public void action(){
        System.out.println("start");
        step();
        System.out.println("end");
    }
    private static final int MAX_NUM=100;
    private int[] arr=new int[MAX_NUM];
    private int count=0;
    @Override
    public void add(int num){
        if(count<MAX_NUM){
            arr[count++]=num;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
