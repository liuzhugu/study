package org.liuzhugu.javastudy.framestudy.spring.proxy.cglib;

import org.liuzhugu.javastudy.framestudy.spring.proxy.staticproxy.UserDao;


public class App {

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        UserDao factory = (UserDao)new ProxyFactory(userDao).getProxyInstance();
        factory.save();
        factory.delete();
    }
}
