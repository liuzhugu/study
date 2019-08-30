package org.liuzhugu.javastudy.book.worldviewinthecode;

import org.springframework.stereotype.Component;

@Component
public class Line extends Shape{
    @Override
    void acceptVisitor(IVisitor visitor) {
        visitor.visit(this);
    }
}
