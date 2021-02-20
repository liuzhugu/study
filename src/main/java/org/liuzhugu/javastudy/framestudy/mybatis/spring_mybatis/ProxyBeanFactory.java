package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyBeanFactory implements FactoryBean<IUserDao> {

    /**
     * 返回bean实例对象
     * */
    @Override
    public IUserDao getObject() throws Exception {
        //返回被代理之后的类
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] clazz = {IUserDao.class};
        InvocationHandler handler = ((proxy, method, args) -> "你被代理了 " + method.getName());

        return (IUserDao) Proxy.newProxyInstance(classLoader,clazz,handler);
    }

    /**
     * 返回实例类类型
     * */
    @Override
    public Class<?> getObjectType() {
        return IUserDao.class;
    }

    /**
     * 判断是否单例,单例会放到Spring容器的单实例缓存池中
     * */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
