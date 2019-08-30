package org.liuzhugu.javastudy.framestudy.spring.proxy.study;

import org.springframework.stereotype.Component;

@Component
public class UserDao implements IUser{

    //第4版 去掉显式引用,做到最轻侵入
    public void save() {
        System.out.println("-----已经保存数据！！！------");
    }

    //第1版
//    public void save() {
//        System.out.println("开启事务");
//        System.out.println("-----已经保存数据！！！------");
//        System.out.println("关闭事务");
//    }
//    public void delete() {
//        System.out.println("开启事务");
//        System.out.println("-----已经保存数据！！！------");
//        System.out.println("关闭事务");
//    }

    //第2版 将重复使用的抽取出来,实现复用
//    public void save() {
//        begin();
//        System.out.println("-----已经保存数据！！！------");
//        close();
//    }
//    public void delete() {
//        begin();
//        System.out.println("-----已经删除数据！！！------");
//        close();
//    }
//    public void begin() {
//        System.out.println("开启事务");
//    }
//    public void close() {
//        System.out.println("关闭事务");
//    }

    //第3版 将重复使用的抽取出来放到一个类去  实现类间共享
//    AOP aop = new AOP();
//    public void save() {
//        aop.begin();
//        System.out.println("-----已经保存数据！！！------");
//        aop.close();
//    }
//    public void delete() {
//        aop.begin();
//        System.out.println("-----已经删除数据！！！------");
//        aop.close();
//    }

}
