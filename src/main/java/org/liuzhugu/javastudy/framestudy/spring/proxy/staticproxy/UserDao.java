package org.liuzhugu.javastudy.framestudy.spring.proxy.staticproxy;

public  class UserDao implements IUserDao {


    //    //重复代码太多,把公共代码抽取出来弄成模板,不同部分通过代理类来调用
    //    @Override
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
    //    public void update() {
    //        System.out.println("开启事务");
    //        System.out.println("-----已经保存数据！！！------");
    //        System.out.println("关闭事务");
    //    }
    //    public void login() {
    //        System.out.println("开启事务");
    //        System.out.println("-----已经保存数据！！！------");
    //        System.out.println("关闭事务");
    //    }

    //只保留单独部分,公共部分抽取到代理类的模板中
    @Override
    public void save() {
        System.out.println("-----已经保存数据！！！------");
    }
    public void delete() {
        System.out.println("-----已经保存数据！！！------");
    }
    public void update() {
        System.out.println("-----已经保存数据！！！------");
    }
    public void login() {
        System.out.println("-----已经保存数据！！！------");
    }
}