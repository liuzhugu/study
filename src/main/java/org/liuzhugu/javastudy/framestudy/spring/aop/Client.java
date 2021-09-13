package org.liuzhugu.javastudy.framestudy.spring.aop;


import org.liuzhugu.javastudy.framestudy.spring.aop.impl.ServiceImpl;
import org.liuzhugu.javastudy.sourcecode.spring.ApplicationContext;
import org.liuzhugu.javastudy.sourcecode.spring.ClassPathXmlApplicationContext;
import org.liuzhugu.javastudy.sourcecode.spring.GenericXmlApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args) {
        testPointCut();
    }
    private static void testPointCut() {
        //创建上下文的时候会通过getBean获取所有非延迟的bean
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:/spring/META-INF/application.xml");
        Biz biz = applicationContext.getBean("proxyFactoryBean", Biz.class);
        biz.help();
        System.out.println("------分割线-----");
        biz.service();
    }

    private static void testProxy() {
        //要代理的真实对象
        Service service = new ServiceImpl();
        //要代理哪个真实对象  就把该对象传进去  最后通过该真实对象来调用其方法
        InvocationHandler handler = new DynamicProxy(service);
        //添加以下的几段代码  就可以将代理生成的字节码保存起来
        try {
            //生成代理类  在接口的某个实现类的基础上  织入其他逻辑  生成该接口的子类作为代理类
            Service serviceProxy = (Service) Proxy.newProxyInstance(service.getClass().getClassLoader(),
                    service.getClass().getInterfaces(),handler);
            serviceProxy.help();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
