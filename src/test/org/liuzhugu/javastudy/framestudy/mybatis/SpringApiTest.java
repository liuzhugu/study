package org.liuzhugu.javastudy.framestudy.mybatis;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liuzhugu.javastudy.framestudy.mybatis.dao.ISchoolDao;
import org.liuzhugu.javastudy.framestudy.mybatis.dao.IUserDao;
import org.liuzhugu.javastudy.framestudy.mybatis.model.School;
import org.liuzhugu.javastudy.framestudy.mybatis.model.User;
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
    private ISchoolDao schoolDao;

    @Resource
    private IUserDao userDao;

    @Test
    public void test_queryRuleTreeByTreeId() {
        School ruleTree = schoolDao.querySchoolInfoById(1);
        System.out.println(JSON.toJSONString(ruleTree));

        User user = userDao.queryUserInfoById(1);
        System.out.println(JSON.toJSONString(user));
    }
}
