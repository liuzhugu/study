package org.liuzhugu.javastudy.logicjava.chapter4;

/**
 * Created by liuting6 on 2018/1/31.
 */
public abstract class Shape {
    private static String DEFAULT_COLOR="black";
    private String color;
    public Shape(){
        this(DEFAULT_COLOR);
    }
    public Shape(String color){
        this.color=color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public abstract void draw();
}
