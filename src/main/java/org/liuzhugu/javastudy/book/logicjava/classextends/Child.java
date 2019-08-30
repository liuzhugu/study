package org.liuzhugu.javastudy.book.logicjava.classextends;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class Child extends Base{
//    public static String s="child_base";
//    public String m="child";
//    public  void staticTest(){
//        System.out.println("child static:"+s);
//    }
//    public void test(long l){
//        System.out.println("child");
//    }
    public static int s;
    private int a;
    static {
        System.out.println("子类静态初始块，s: "+s);
        s=10;
    }
    {
        System.out.println("子类初始块,a: "+a);
        a=10;
    }
    public Child(){
        System.out.println("子类构造方法,a: "+a);
        a=20;
    }
    protected void step(){
        System.out.println("child s: "+s+",a: "+a);
    }
}
