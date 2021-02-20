package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
public class Test {
    public static void main(String[] args) {

        //1.new方式实例化
        //IUserDao userDao = new UserDao();
        //System.out.println(userDao.queryUserInfo());

        //proxy方式实例化
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] classes = {IUserDao.class};
        InvocationHandler handler = ((proxy, method, args1) -> "你被代理了 "
            + method.getName());

        IUserDao userDao = (IUserDao) Proxy.newProxyInstance(classLoader,classes,handler);

        System.out.println("测试结果为: " + userDao.queryUserInfo());
    }
}
