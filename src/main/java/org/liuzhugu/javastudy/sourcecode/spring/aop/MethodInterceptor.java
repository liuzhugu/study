package org.liuzhugu.javastudy.sourcecode.spring.aop;


import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Callback {
    Object intercept(Object var1, Method var2, Object[] var3, MethodProxy var4) throws Throwable;
}
