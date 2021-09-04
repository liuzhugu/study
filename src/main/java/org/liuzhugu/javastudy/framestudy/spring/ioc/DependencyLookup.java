package org.liuzhugu.javastudy.framestudy.spring.ioc;

import org.liuzhugu.javastudy.sourcecode.spring.BeanFactory;
import org.liuzhugu.javastudy.sourcecode.spring.ClassPathXmlApplicationContext;
import org.liuzhugu.javastudy.sourcecode.spring.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 查找bean
 * */
public class DependencyLookup {
    @Autowired
    private User user;

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext(
                "classpath:/spring/META-INF/dependency-lookup.xml");

        //bean加载与查找
        //根据bean名查找
        //lookupRealtime(beanFactory);
        //根据类型查找  同一类型可能有多个bean
        //   获取一个
        //lookupSingleByType(beanFactory);
        //   获取所有
        //lookupCollectionByType(beanFactory);

        //依赖注入   可以通过名称或者类型找到依赖的bean 然后注入需要的地方
        TestUtil testUtil = (TestUtil) beanFactory.getBean("testUtil");
        User user = testUtil.getUser();
        System.out.println(user);

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
