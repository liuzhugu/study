package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

import java.io.Serializable;

public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice, Serializable {
    private final AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }
}