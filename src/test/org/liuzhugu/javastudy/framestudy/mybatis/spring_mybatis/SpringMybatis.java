package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.liuzhugu.javastudy.framestudy.mybatis.dao.IUserDao;
import org.liuzhugu.javastudy.framestudy.mybatis.model.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMybatis {
    @Test
    public void test_IUserDao() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("mybatis/spring-config.xml");
        IUserDao userDao = beanFactory.getBean("userDao",IUserDao.class);

        System.out.println("测试结果为: " + userDao.queryUserInfoById(1));
    }

    //自己扫描   自己注册dao
    @Test
    public void test_ClassPathXmlApplicationContext() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("mybatis/spring/test-config.xml");
        IUserDao userDao = beanFactory.getBean("IUserDao",IUserDao.class);
        User user = userDao.queryUserInfoById(1);
        System.out.println(JSON.toJSONString(user));
    }
}
