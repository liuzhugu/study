package org.liuzhugu.javastudy.framestudy.spring.proxy.study;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    //目标对象
    private static Object target;
    //关键点代码对象
    private static AOP aop;

    //这部分代码对用户屏蔽,用户只用专注于自己想要的方法的实现就行,通过代理工厂按照模板自动织入指定代码
    public static Object getProxyInstance(Object target_,AOP aop_) {
        //外界传入,编织的代码模板是确定的,但用户可以通过控制传入代码来实现灵活定义
        target = target_;
        aop = aop_;

        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        //自动编织代码
                        aop.begin();
                        Object returnValue = method.invoke(target,args);
                        aop.close();

                        return returnValue;
                    }
                }
        );
    }

}
