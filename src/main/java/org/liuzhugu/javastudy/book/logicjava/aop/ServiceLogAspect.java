package org.liuzhugu.javastudy.book.logicjava.aop;

import org.liuzhugu.javastudy.book.logicjava.annotation.ServiceA;
import org.liuzhugu.javastudy.book.logicjava.annotation.ServiceB;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect({ServiceA.class, ServiceB.class})
public class ServiceLogAspect {

    public static void before(Object obj, Method method,Object[] args) {
        System.out.println("entering " + method.getDeclaringClass().getSimpleName()
                + "::" + method.getName() + ",args: " + Arrays.toString(args));
    }

    public static void after(Object obj, Method method,Object[] args,Object result) {
        System.out.println("entering " + method.getDeclaringClass().getSimpleName()
                + "::" + method.getName() + ",args: " + Arrays.toString(args)
                + ",result: " + result);
    }
}
