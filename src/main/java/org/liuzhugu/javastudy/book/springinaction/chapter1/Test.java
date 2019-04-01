package org.liuzhugu.javastudy.book.springinaction.chapter1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {
        //加载上下文
        ClassPathXmlApplicationContext applicationContext = new
                ClassPathXmlApplicationContext("/springinaction/chapter1/knight.xml");
        //使用上下文获取bean
        Knight knight = applicationContext.getBean(Knight.class);
        //使用bean
        knight.embarkOnQuest();
        applicationContext.close();
    }
}
