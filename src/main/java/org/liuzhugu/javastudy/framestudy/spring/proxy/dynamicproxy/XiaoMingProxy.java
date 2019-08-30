package org.liuzhugu.javastudy.framestudy.spring.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class XiaoMingProxy {
    //动态代理和静态代理的区别

    XiaoMing xiaoMing = new XiaoMing();

    //返回代理对象
    public Person getProxy(){
        //静态代理和动态代理的区别在此
        //静态代理相当于门面模式,包装了下被代理的类,对外表现得跟被代理类一样
        //动态代理比静态代理好的地方时配置好模板之后可以通过反射动态生成,而静态代理类实际调用被代理类时需要写死
        return (Person) Proxy.newProxyInstance(XiaoMingProxy.class.getClassLoader(), xiaoMing.getClass().getInterfaces(),
                new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("sing")) {
                    System.out.println("给1000万来再唱");

                    //实际唱歌的还是小明
                    method.invoke(xiaoMing,args);
                }
                return null;
            }
        });
    }
}
