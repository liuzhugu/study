package org.liuzhugu.javastudy.logicjava.chapter4;

import org.liuzhugu.javastudy.logicjava.chapter2.Point;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class Line extends Shape{
    private Point start;
    private Point end;
    public Line(Point start,Point end){
        this.start=start;
        this.end=end;
    }
    public Line(Point start,Point end,String color){
        //super(color);
        super(color);
        this.start=start;
        this.end=end;
    }
    public int length(){
        return (int)Math.sqrt(Math.pow((double) (start.getX()-end.getX()),2)+Math.pow((double) (start.getY()-end.getY()),2));
    }
    @Override
    public void draw(){
        System.out.println("draw line from "+start.toString()+" to "+end.toString()+" length "+length()+" color "+getColor());
    }
}
