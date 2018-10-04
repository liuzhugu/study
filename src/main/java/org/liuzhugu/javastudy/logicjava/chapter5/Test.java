package org.liuzhugu.javastudy.logicjava.chapter5;

import org.liuzhugu.javastudy.logicjava.chapter2.Point;
import org.liuzhugu.javastudy.logicjava.chapter4.Base;

import java.util.Arrays;

/**
 * Created by liuting6 on 2018/2/7.
 */
public class Test {
    public static void main(String[] args){
//        Point first=new Point(3,4);
//        Point second=new Point(4,3);
//        MyComparable first=new Point(3,4);
//        MyComparable second=new Point(3,4);
//        System.out.println(first.compareTo(second));
//        Point[] points=new Point[]{new Point(3,4),new Point(5,3),new Point(2,3)};
//        System.out.println(CompUtil.max(points));
//        CompUtil.sort(points);
//        System.out.println("sort"+ Arrays.toString(points));
//        IAdd test=new Base();
//        test.addAll(new int[]{1,3,5,7,9,2,4,6,8});
//        new Outer().new Inner().innerMethod();
//        (new Outer.StaticInner()).innerMethod();
//        System.out.println();
        //(new Outer()).test(3,4);
        Size size=Size.LARGE;
        System.out.println(size.toString());
        System.out.println(size.name());
        System.out.println(size==Size.LARGE);
        System.out.println(size.equals(Size.LARGE));
        System.out.println(size.ordinal());
        System.out.println(size.compareTo(Size.LARGE));
        switch (size){
            case SMALL:
                System.out.println("chosen small");
                break;
            case MEDIUM:
                System.out.println("chosen medium");
                break;
            case LARGE:
                System.out.println("chosen large");
        }
        System.out.println(Size.LARGE==Size.valueOf("LARGE"));
        for(Size size1:Size.values()){
            System.out.println(size1);
        }
    }
}
