package org.liuzhugu.javastudy.book.logicjava.annotation;

@Liuzhuzhu(info = {"依赖的服务"})
public class ServiceA {

    //服务A依赖于服务B,等待容器把服务B注入
    @SingleInject
    ServiceB serviceB;
    public void callB() {
        serviceB.action();
    }
}
