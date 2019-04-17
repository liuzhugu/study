package org.liuzhugu.javastudy.book.worldviewinthecode;

public class MyWindow implements Window{
    @Override
    public void draw(Shape shape) {
        //表面上工作的是window,但实际工作的是shape
        //只不过跟依赖的区别是这里还需要把window自己也当参数传过去
        shape.draw(this);
    }
}
