package org.liuzhugu.javastudy.book.worldviewinthecode;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class CodeTest {
    public static void main(String[] args) {
        //双向依赖,表面上是window具有了draw的能力,但其实真正工作的是shape
        //Window window = new MyWindow();
        //window.draw(new Shape());

        //访问者模式,通过方法的多态在语言级别实现参数类型匹配,从而完成扩展

        //可以通过spring把相应加了注解的实现类加载进来设置进该列表,从而进一步保持客户端代码的不变性
        ClassPathXmlApplicationContext context = new
                ClassPathXmlApplicationContext("/designpatterns/chain/bean.xml");
        Map<String, Shape> shapes = context.getBeansOfType(Shape.class);
        //List<Shape> shapes = Arrays.asList(new Rectangle(),new Circle(),new Line());
        //更换IVisitor的实现类相当于更换策略,但shape的实现类却全部不用变
        //IVisitor iVisitor = new ZoomInVisitor();
        IVisitor iVisitor = new ZoomOutVisitor();
        for (Map.Entry<String,Shape> entry : shapes.entrySet()) {
            //如果要实现扩展,那么得改下面两个地方
            // 而其他的代码不需要变,无论是增减任何图形,都可以不改变这里的客户端代码而轻松扩展
            //1.给Shape增加实现类,并且给实现类加注解,让其能被spring扫描进来
            //2.IVisitor的实现类中增加参数为这个实现类的新方法
            entry.getValue().acceptVisitor(iVisitor);
        }
    }
}
