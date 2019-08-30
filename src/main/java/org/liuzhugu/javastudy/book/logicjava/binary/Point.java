package org.liuzhugu.javastudy.book.logicjava.binary;

import org.liuzhugu.javastudy.book.logicjava.classexpansion.MyComparable;

/**
 * Created by liuting6 on 2018/1/29.
 */
public class Point implements MyComparable{
    private int x;
    private int y;
    public Point(){
        //x=0       //报错，this调用其他构造方法必须在第一句
        this(0,0);
    }
    public Point(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double distance(){
        return Math.sqrt(x*x+y*y);
    }
    public double distance(Point point){
        return Math.sqrt(point.getX()*point.getX()+point.getY()*point.getY());
    }

    @Override
    public String toString(){
        return "x: "+getX()+",y: "+getY();
    }
    @Override
    public  int compareTo(Object other){
        if(!(other instanceof Point)){
            throw new IllegalArgumentException();
        }
        Point otherPoint=(Point)other;
        double delta=distance()-otherPoint.distance();
        if(delta<0){
            return -1;
        }else if(delta>0){
            return 1;
        }else {
            return 0;
        }
    }
}
