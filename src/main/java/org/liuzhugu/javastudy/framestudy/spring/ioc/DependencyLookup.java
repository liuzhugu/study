package org.liuzhugu.javastudy.framestudy.spring.ioc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * 查找bean
 * */
public class DependencyLookup {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:/spring/META-INF/dependency-lookup.xml");
        //根据bean名查找
        lookupRealtime(beanFactory);
        //根据类型查找  同一类型可能有多个bean
        //   获取一个
        lookupSingleByType(beanFactory);
        //   获取所有
        lookupCollectionByType(beanFactory);
    }

    private static void lookupRealtime(BeanFactory beanFactory) {
        User user = (User) beanFactory.getBean("user");
        System.out.println(user);
    }

    private static void lookupSingleByType(BeanFactory beanFactory) {
        User user = (User) beanFactory.getBean(User.class);
        System.out.println("根据类型实时查找单一" + user.toString());
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory= (ListableBeanFactory) beanFactory;
            Map<String,User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找多个bean" + users);
        }
    }
}
