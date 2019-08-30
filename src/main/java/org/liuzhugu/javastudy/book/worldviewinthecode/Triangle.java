package org.liuzhugu.javastudy.book.worldviewinthecode;


import org.springframework.stereotype.Component;

//新增加的图形
@Component
public class Triangle extends Shape{
    @Override
    void acceptVisitor(IVisitor visitor) {
        visitor.visit(this);
    }
}
