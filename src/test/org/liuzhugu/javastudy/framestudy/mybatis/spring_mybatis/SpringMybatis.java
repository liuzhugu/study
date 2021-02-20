package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMybatis {
    @Test
    public void test_IUserDao() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("mybatis/spring-config.xml");
        IUserDao userDao = beanFactory.getBean("userDao",IUserDao.class);

        System.out.println("测试结果为: " + userDao.queryUserInfo());
    }
}
