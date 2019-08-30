package org.liuzhugu.javastudy.book.logicjava.proxy;

import org.liuzhugu.javastudy.book.logicjava.annotation.Liuzhuzhu;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Liuzhuzhu(info = "JDK实现动态代理")
public class SimpleJDKDynamicProxyDemo {

    static interface IService {
        public void sayHello();
    }
    static class RealService implements IService {
        @Override
        public void sayHello() {
            System.out.println("hello");
        }
    }

    static interface IServiceFirst {
        public void sayHelloFirst();
    }
    static class RealServiceFirst implements IServiceFirst {
        @Override
        public void sayHelloFirst() {
            System.out.println("hello first");
        }
    }


    static class SimpleInvocationHandler implements InvocationHandler {
        //设置真正干活的类
        private Object realObject;
        public SimpleInvocationHandler(Object realObject) {
            this.realObject = realObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("entering " + method.getName());
            //跟静态的区别是:静态代理这里需要写死调用的方法
            //而现在实现泛用性,传什么方法进来调用什么方法
            Object result = method.invoke(realObject,args);
            System.out.println("leaving " + method.getName());
            return result;
        }
    }

    public static void main(String[] args) {
        IService realService = new RealService();
        IService proxyService = (IService) Proxy.newProxyInstance(
                IService.class.getClassLoader(),new Class<?>[]{IService.class},
                new SimpleInvocationHandler(realService)
        );
        proxyService.sayHello();

        //替换实现类和接口
        System.out.println("替换实现类和接口");
        IServiceFirst realServiceFirst = new RealServiceFirst();
        IServiceFirst proxyFirstService = (IServiceFirst) Proxy.newProxyInstance(
                IServiceFirst.class.getClassLoader(),new Class<?>[]{IServiceFirst.class},
                new SimpleInvocationHandler(realServiceFirst)
        );
        proxyFirstService.sayHelloFirst();

    }
}
