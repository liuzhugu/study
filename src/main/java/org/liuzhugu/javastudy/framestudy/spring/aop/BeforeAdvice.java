package org.liuzhugu.javastudy.framestudy.spring.aop;


import org.liuzhugu.javastudy.sourcecode.spring.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class BeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("方法执行之前增强");
    }
}
