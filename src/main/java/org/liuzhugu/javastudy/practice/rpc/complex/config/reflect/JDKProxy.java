package org.liuzhugu.javastudy.practice.rpc.complex.config.reflect;

import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Request;
import org.liuzhugu.javastudy.practice.rpc.complex.util.ClassLoaderUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理类
 * */
public class JDKProxy {
    public static <T> T getProxy(Class<T> interfaceClass, Request request) throws Exception{
        InvocationHandler handler = new JDKInvocationHandler(request);
        ClassLoader classLoader = ClassLoaderUtils.getCurrentClassLoader();
        T result = (T) Proxy.newProxyInstance(classLoader,new Class[]{interfaceClass},handler);
        return result;
    }
}
