package org.liuzhugu.javastudy.logicjava.chapter4;

import org.liuzhugu.javastudy.logicjava.chapter2.Point;

/**
 * Created by liuting6 on 2018/1/31.
 */
public class ArrowLine extends Line{
    private boolean startArrow;
    private boolean endArrow;
    public ArrowLine(Point start,Point end,String color,boolean startArrow,boolean endArrow){
        super(start,end,color);
        this.startArrow=startArrow;
        this.endArrow=endArrow;
    }
    @Override
    public void draw(){
        super.draw();
        if(startArrow){
            System.out.println("draw start arrow");
        }
        if(endArrow){
            System.out.println("draw end arrow");
        }
    }
}
