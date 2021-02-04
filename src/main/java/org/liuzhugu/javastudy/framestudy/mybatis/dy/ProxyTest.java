package org.liuzhugu.javastudy.framestudy.mybatis.dy;


import java.lang.reflect.Proxy;

public class ProxyTest {
    public static void main(String[] args) {
        //表面看是接口   实际是具体的实现类
        Subject subject = new SubjectImpl();

        //Proxy生成代理类  通过代理类来完成任务
        Subject proxy = (Subject) Proxy.newProxyInstance(
                subject.getClass().getClassLoader(),
                new Class[]{Subject.class},
                new ProxyInvocationHandler()
        );
        String resultStr = proxy.sayHello();
        System.out.println(resultStr);
    }
}
