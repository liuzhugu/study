package org.liuzhugu.javastudy.book.worldviewinthecode;

import org.springframework.stereotype.Component;

@Component
public class Rectangle extends Shape {
    @Override
    void acceptVisitor(IVisitor visitor) {
        visitor.visit(this);
    }
}
