package org.liuzhugu.javastudy.framestudy.mybatis;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.liuzhugu.javastudy.framestudy.mybatis.model.User;

import java.io.IOException;
import java.io.Reader;


public class MybatisApiTest {


    /**
     * mybatis的两大核心   XML 和  接口
     * 根据XML为接口生成实现类
     * */
    @Test
    public void test_queryUserInfoById() {
        String resource = "mybatis/spring/mybatis-config-datasource.xml";
        Reader reader;
        try {
            reader = Resources.getResourceAsReader(resource);
            //从xml中读取配置  建立SqlSessionFactory
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
            //开启会话
            SqlSession session = sqlMapper.openSession();
            try {
                //为接口生成代理类来实现
                User user = session.selectOne("org.liuzhugu.javastudy.framestudy." +
                        "mybatis.dao.IUserDao.queryUserInfoById",1);
                System.out.println(JSON.toJSONString(user));
            } finally {
                session.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
