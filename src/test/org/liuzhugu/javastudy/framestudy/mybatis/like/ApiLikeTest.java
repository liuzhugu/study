package org.liuzhugu.javastudy.framestudy.mybatis.like;




import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.liuzhugu.javastudy.framestudy.mybatis.model.User;


import java.io.Reader;

/**
 * 自制简易版mybatis
 *
 * */
public class ApiLikeTest {
    @Test
    public void test_queryUserInfoById() {
        String resource = "mybatis/spring/mybatis-config-datasource.xml";
        Reader reader;
        try {
            //1.读取XML
            reader = Resources.getResourceAsReader(resource);
            //2.解析XML  生成Document
            //3.根据Document生成配置
            //4.根据配置设置SqlSessionFactory
                //4.1 根据配置设置数据源
                //4.2 通过数据源获取连接
                //4.3 设置mapperMap  之后可以根据key获取我们在XML是如何配置mapper的
                    //4.3.1 命名空间加id作为key  唯一确定
                    //4.3.2 将方法名 参数类型 返回值类型 还有SQL保存起来
                        //其中SQL中字段的占位符被替换成 ?
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
            //5. 通过连接开启会话
            SqlSession session = sqlMapper.openSession();

            try {
                //6.根据key查找对应的mapper
                    //6.1 根据key找到我们在XML文件中配置的mapper
                    //6.2 根据传进去的参数和我们在XML文件中对参数配置一起组装参数
                    //6.3 执行sql 获得返回值
                    //6.4 将得到的结果根据我们在XML文件中对返回结果的配置一起组装返回值
                    //6.5 返回结果
                User user =  session.selectOne(
                        "org.liuzhugu.javastudy.framestudy.mybatis.dao.IUserDao.queryUserInfoById",1);
                System.out.println(JSON.toJSONString(user));
            } finally {
                session.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
