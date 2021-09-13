package org.liuzhugu.javastudy.framestudy.spring.aop.impl;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class BizInterceptor implements MethodInterceptor {
    //Object是生成的子类对象  Method是要代理目标类的方法
    // Object[] 是参数  MethodProxy子类生成的代理方法
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("买书前");
        //调用父类方法  也就是目标类
        methodProxy.invokeSuper(o,args);
        System.out.println("买书后");
        return null;
    }
}
