package org.liuzhugu.javastudy.framestudy.spring.proxy.staticproxy;

public class Test {
    public static void main (String[] args) {
        //目标对象
        IUserDao target = new UserDao();

        //代理
        IUserDao proxy = new UserDaoProxy(target);
        proxy.save();
    }
}
