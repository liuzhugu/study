package org.liuzhugu.javastudy.framestudy.mybatis;

import org.junit.runner.RunWith;
import org.liuzhugu.javastudy.framestudy.mybatis.dao.IUserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mybatis/spring-config.xml")
public class SpringApiTest {

    private Logger logger = LoggerFactory.getLogger(SpringApiTest.class);



    @Resource
    private IUserDao userDao;

}
