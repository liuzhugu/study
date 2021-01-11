package org.liuzhugu.javastudy.book.worldviewinthecode;

public class ZoomInVisitor implements IVisitor{

    //相比于每个shape都覆写方法的好处是集中到了这里,一方面可以共用某些方法
    // 另一方面相同逻辑的代码聚集起来改起来思路清晰一点,并且有变更的话可以一目了然地考虑所有情况
    @Override
    public void visit(Rectangle rectangle) {
        System.out.println("放大长方形");
    }

    @Override
    public void visit(Line line) {
        System.out.println("放大线");
    }

    @Override
    public void visit(Circle circle) {
        System.out.println("放大圆");
    }

    @Override
    public void visit(Triangle triangle) {
        System.out.println("放大三角形");
    }
}
