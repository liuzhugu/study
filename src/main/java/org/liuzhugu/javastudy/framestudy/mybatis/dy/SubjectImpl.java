package org.liuzhugu.javastudy.framestudy.mybatis.dy;

import org.liuzhugu.javastudy.framestudy.mybatis.dy.Subject;

/**
 * 实现类
 * */
public class SubjectImpl implements Subject {
    @Override
    public String sayHello() {
        System.out.println("Hello world");
        return "success";
    }
}
