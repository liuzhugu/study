package org.liuzhugu.javastudy.book.worldviewinthecode;

public abstract class Shape {
    void draw(Window window){
        System.out.println("draw window");
    }
    abstract void acceptVisitor(IVisitor visitor);
}
