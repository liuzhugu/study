package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import org.liuzhugu.javastudy.framestudy.mybatis.mylike.SqlSession;
import org.liuzhugu.javastudy.framestudy.mybatis.mylike.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * */
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;
    private SqlSessionFactory sqlSessionFactory;

    public MapperFactoryBean(Class<T> mapperInterface, SqlSessionFactory sqlSessionFactory) {
        this.mapperInterface = mapperInterface;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public T getObject() throws Exception {
        //代理
        InvocationHandler handler = (proxy, method, args) -> {
            System.out.println("你被代理了,执行SQL操作!" +   method.getName());
            try {
                SqlSession sqlSession = sqlSessionFactory.openSession();
                try {
                    return sqlSession.selectOne(mapperInterface.getName() + "." + method.getName(),args[0]);
                } finally {
                    sqlSession.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return method.getReturnType().newInstance();
        };
        //生成代理类
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{mapperInterface},handler);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
