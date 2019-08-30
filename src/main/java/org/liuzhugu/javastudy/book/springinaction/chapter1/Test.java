package org.liuzhugu.javastudy.book.springinaction.chapter1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {
        //1.通过类路径下的XML加载应用上下文上下文
        ClassPathXmlApplicationContext context = new
                ClassPathXmlApplicationContext("/springinaction/chapter1/knight.xml");
        //2.通过注解加载应用上下文
        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        //context.register(KnightConfig.class);
        //context.refresh();

        //使用上下文获取bean
        Knight knight = context.getBean(Knight.class);
        //使用bean
        knight.embarkOnQuest();
        context.close();
    }
}
