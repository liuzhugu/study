package org.liuzhugu.javastudy.book.worldviewinthecode;

public interface IVisitor {
    void visit(Rectangle rectangle);
    void visit(Line line);
    void visit(Circle circle);
    void visit(Triangle triangle);
}
