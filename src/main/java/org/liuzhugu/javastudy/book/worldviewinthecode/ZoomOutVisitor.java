package org.liuzhugu.javastudy.book.worldviewinthecode;

public class ZoomOutVisitor implements IVisitor{


    @Override
    public void visit(Rectangle rectangle) {
        System.out.println("缩小长方形");
    }

    @Override
    public void visit(Line line) {
        System.out.println("缩小线");
    }

    @Override
    public void visit(Circle circle) {
        System.out.println("缩小圆");
    }

    @Override
    public void visit(Triangle triangle) {
        System.out.println("缩小三角形");
    }
}
