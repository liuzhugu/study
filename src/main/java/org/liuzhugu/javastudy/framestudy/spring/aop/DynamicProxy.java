package org.liuzhugu.javastudy.framestudy.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxy implements InvocationHandler {
    //代理的真实对象
    private Object service;
    //构造方法
    public DynamicProxy(Object service) {
        this.service = service;
    }

    //生成代理类  将切面逻辑织入代理类中
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //真实方法之前执行
        System.out.println("买书前");
        //调用真实方法
        method.invoke(service,args);
        //真实方法之后执行
        System.out.println("买书后");
        return null;
    }
}
