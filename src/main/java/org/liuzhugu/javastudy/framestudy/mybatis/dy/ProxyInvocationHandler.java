package org.liuzhugu.javastudy.framestudy.mybatis.dy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 动态代理调用处理器
 * */
public class ProxyInvocationHandler implements InvocationHandler {

//    private Object targert;
//
//    public ProxyInvocationHandler (Object  targert) {
//        this.targert = targert;
//    }

    /**
     * 瞒天过海的地方
     * */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("----进入代理调用处理器----");
//        return method.invoke(targert,args);
        return "success";
    }
}
