package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import org.liuzhugu.javastudy.framestudy.mybatis.mylike.Resources;
import org.liuzhugu.javastudy.framestudy.mybatis.mylike.SqlSessionFactory;
import org.liuzhugu.javastudy.framestudy.mybatis.mylike.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Reader;

public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean {

    private String resource;

    //解析 构造  连接数据库
    public void setResource(String resource) {
        this.resource = resource;
    }

    private SqlSessionFactory sqlSessionFactory;


    //配置设置好后  开始生成
    @Override
    public void afterPropertiesSet() throws Exception {
        try (Reader reader = Resources.getResourceAsReader(resource)){
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        return sqlSessionFactory;
    }

    //bean的类型
    @Override
    public Class<?> getObjectType() {
        return sqlSessionFactory.getClass();
    }

    //单例
    @Override
    public boolean isSingleton() {
        return true;
    }
}
