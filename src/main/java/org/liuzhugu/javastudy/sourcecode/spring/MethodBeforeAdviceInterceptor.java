package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

import java.io.Serializable;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor, Serializable {
    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        //模板模式  before被相应拦截器覆盖掉 然后到了运行时  就相当于运行切面的before方法再运行真正的方法
        this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}