package org.liuzhugu.javastudy.sourcecode.spring;


import java.lang.reflect.Method;

public interface AfterReturningAdvice extends AfterAdvice {
    void afterReturning(Object var1, Method var2, Object[] var3, Object var4) throws Throwable;
}