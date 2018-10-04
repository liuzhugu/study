package org.liuzhugu.javastudy.logicjava.chapter5;

import org.liuzhugu.javastudy.logicjava.chapter2.Point;

/**
 * Created by liuting6 on 2018/2/8.
 */
public class Outer {
    private static int shared=100;
    private int shared1=200;
    //静态内部类
    public static class StaticInner{
        public void innerMethod(){
            System.out.println("----static inner----");
            System.out.println("static inner "+shared);
            staticMethod();
        }
    }
    //内部类
    public class Inner{
        public void innerMethod(){
            System.out.println("----inner----");
            System.out.println("static inner "+shared);
            System.out.println("inner "+shared1);
            staticMethod();
            method();
        }
    }
    public  void test(final int param){
        final String str="hello";
        //方法内部类
        class MethodInner{
            public void innerMethod(){
                System.out.println("outer static:"+shared);
                System.out.println("param:"+param);
                System.out.println("str:"+str);
            }
        }
        MethodInner methodInner=new MethodInner();
        methodInner.innerMethod();
    }
    //匿名内部类
    public void test(final int x,final int y){
        Point point=new Point(3,4){
            @Override
            public double distance(){
                return distance(new Point(x,y));
            }
        };
        System.out.println(point.distance());
    }
    public static void staticMethod(){
        System.out.println("static method");
    }
    public void method(){
        System.out.println("method");
    }
}
