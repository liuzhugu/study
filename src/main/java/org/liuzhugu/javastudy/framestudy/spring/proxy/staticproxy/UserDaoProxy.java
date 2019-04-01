package org.liuzhugu.javastudy.framestudy.spring.proxy.staticproxy;

import org.liuzhugu.javastudy.framestudy.spring.proxy.staticproxy.IUserDao;

public class UserDaoProxy implements IUserDao {

    //实际执行的类
    private IUserDao target;
    public UserDaoProxy(IUserDao target) {
        this.
                target = target;
    }

    @Override
    public void save() {
        System.out.println("开始事务...");
        //运行时传什么就来就执行什么
        target.save();
        System.out.println("提交事务...");
    }
}
