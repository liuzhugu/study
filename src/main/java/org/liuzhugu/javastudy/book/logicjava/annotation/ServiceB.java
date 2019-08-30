package org.liuzhugu.javastudy.book.logicjava.annotation;

@Liuzhuzhu(info = {"被依赖的服务"})
public class ServiceB {
    public void action() {
        System.out.println("I'm B");
    }
}
