package org.liuzhugu.javastudy.framestudy.spring.aop;


import org.aopalliance.intercept.MethodInvocation;
import org.liuzhugu.javastudy.sourcecode.spring.MethodInterceptor;

public class ArroundAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("方法执行之前增强");
        Object ret = invocation.proceed();
        System.out.println("方法执行之后增强");
        return ret;
    }
}
