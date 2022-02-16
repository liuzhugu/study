package org.liuzhugu.javastudy.framestudy.spring.aop.impl;

import org.liuzhugu.javastudy.framestudy.spring.aop.RealService;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyServiceInterceptor implements MethodInterceptor {
    public static void main(String[] args) {
        //设置代理类生成目录
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"D://proxy");
        Enhancer enhancer = new Enhancer();
        //设置超类  因为cglib基于父类  生成代理子类
        enhancer.setSuperclass(RealService.class);
        // 设置回调  也就是我们的拦截处理
        enhancer.setCallback(new MyServiceInterceptor());

        //创建代理类
        RealService realService = (RealService) enhancer.create();
        //调用代理类的方法
        realService.realMethod();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("before execute");
        Object result = methodProxy.invokeSuper(obj,objects);
        System.out.println("after execute");
        return result;
    }
}
