package org.liuzhugu.javastudy.book.logicjava.chapter4;

import org.liuzhugu.javastudy.book.logicjava.chapter2.Point;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class Circle  extends Shape{
    private Point center;
    private double r;
    public Circle(Point center,double r){
        this.center=center;
        this.r=r;
    }
    @Override
    public void draw(){
        System.out.println("draw circle at "+center.toString()+" with r "+r+" area "+area()+" using color "+getColor());
    }
    public int area(){
        return (int)(Math.PI*r*r);
    }

}
