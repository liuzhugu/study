package org.liuzhugu.javastudy.book.logicjava.chapter4;

import org.liuzhugu.javastudy.book.logicjava.chapter2.Point;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class Test {
    static double afloat;
    public static void main(String[] args){
//        Point center=new Point(1,1);
//        Circle circle=new Circle(center,3);
//        circle.draw();
//        Point start=new Point(1,2);
//        Point end=new Point(5,5);
//        Line line=new Line(start,end);
//        line.draw();
//        Shape shape=new Shape();
//        ShapeManeger shapeManeger=new ShapeManeger();
//        shapeManeger.addShape(shape);
//        Point first=new Point(1,1);
//        Point second=new Point(3,5);
//        Point third=new Point(6,5);
//        shape=new Circle(first,3);
//        shapeManeger.addShape(shape);
//        shape=new Line(second,third,"blue");
//        shapeManeger.addShape(shape);
//        shape=new ArrowLine(second,third,"red",false,true);
//        shapeManeger.addShape(shape);
//        shapeManeger.draw();
//        Child child=new Child();
//        Base base=new Base();
        //绑定
//        System.out.println(child.m);
//        System.out.println(child.s);
//        child.staticTest();
//        System.out.println(base.m);
//        System.out.println(base.s);
//        base.staticTest();
//        Base base1=new Child();
//        System.out.println(base1.m);
//        System.out.println(base1.s);
//        base1.staticTest();
//        char c=0;
//        int i=0;
//        long l=0;
        //c l l 第二个向上兼容了
//        base.test(c);
////        base.test(i);
////        base.test(l);
//        System.out.println();
//        //c i l
//        child.test(c);
//        child.test(i);
//        //child.test(l);
//        System.out.println();
//        base=child;
//        base.test(c);
//        base.test(i);
//        base=child;
//        base.test(i);
        //base.test(l);
//        base=child;
//
//          child=(Child)base;
//          child.test(c);
        //初始化顺序
//        System.out.println("----new Child");
//        Child child=new Child();
//        System.out.println("----c.action");
//        child.action();
//        Base base=child;
//        System.out.println("----b.action");
//        base.action();
//        System.out.println("\n----base.s: "+base.s);
//        System.out.println("\n----child.s: "+child.s);
        //System.out.println(afloat);
        Shape shape=new Line(new Point(3,4),new Point(1,2));
        shape.draw();
    }
}
