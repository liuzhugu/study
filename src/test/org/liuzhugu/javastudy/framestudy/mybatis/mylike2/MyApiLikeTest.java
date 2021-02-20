package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;


import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.liuzhugu.javastudy.framestudy.mybatis.model.User;

import java.io.Reader;

/**
 * 自制简易版mybatis
 *
 * */
public class MyApiLikeTest {

    @Test
    public void test_queryUserInfoById() {
        //1.读取XML
        String resource = "mybatis/spring/mybatis-config-datasource.xml";
        Reader reader;
        try {
            reader = Resources.getResourceAsReader(resource);
            //2.解析XML 生成Document
            //3.根据Document生成Configuration
            //4.根据Configuration设置SqlSessionFactory
                //4.1 根据Configuration配置数据源
                //4.2 从数据源中获取连接
                //4.2 为所有mapper建立映射
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            //5.通过连接开启会话
            SqlSession sqlSession = sqlSessionFactory.openSession();
            //6.执行sql
            try {
                //6.1 根据key获取对应的mapper
                //6.2 根据mapper里的配置设置参数
                //6.3 执行sql
                //6.4 根据mapper组装返回值
                //6.5  返回结果
                User user = sqlSession.selectOne("org.liuzhugu.javastudy.framestudy.mybatis.dao.IUserDao.queryUserInfoById",1);
                System.out.println(JSON.toJSONString(user));
            } finally {
                //收尾工作  关闭资源
                sqlSession.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
