package org.liuzhugu.javastudy.framestudy.spring.exception;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class DemoApplication {
    public static void main(String[] args) {

        //1.找不到指定bean
//        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
//        ac.register(DemoApplication.class);
//        ac.refresh();
//
//        //NoSuchBeanDefinitionException   不存在要找的bean
//        byBeanFactory(ac);
//        ac.close();

        //2.找到不止一个bean
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(Config.class);
        ac.refresh();

        //NoUniqueBeanDefinitionException  要查找的bean不唯一
        byBeanFactory(ac);
        ac.close();
    }

    //查找bean
    private static void byBeanFactory(AnnotationConfigApplicationContext ac) {
        ac.getBean(Rumenz.class);
    }
}
