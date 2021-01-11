package org.liuzhugu.javastudy.book.logicjava.proxy;

import org.liuzhugu.javastudy.book.logicjava.annotation.Liuzhuzhu;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Liuzhuzhu(info = "cglib动态代理")
public class SimpleCGLibDemo {

    static class RealService {
        public void sayHello() {
            System.out.println("hello");
        }
    }

    static class SimpleInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object realObject, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            System.out.println("entering " + method.getName());
            Object result = methodProxy.invokeSuper(realObject,args);
            System.out.println("leaving " + method.getName());
            return result;
        }
    }

    private static <T> T getProxy(Class<?> cls) {
        Enhancer enhancer = new Enhancer();
        //生成一个对象,这个对象的父类是被代理类
        enhancer.setSuperclass(cls);
        //被代理的类的相应方法被复写了
        enhancer.setCallback(new SimpleInterceptor());
        return (T)enhancer.create();
    }

    public static void main(String[] args) {
        RealService proxyService = getProxy(RealService.class);
        proxyService.sayHello();
    }
}
