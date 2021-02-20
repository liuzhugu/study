package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;


import java.sql.Connection;
import java.util.Map;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory (Configuration configuration) {
        this.configuration = configuration;
    }

    //组装参数
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration.getConnection(),configuration.getMappers());
    }
}
