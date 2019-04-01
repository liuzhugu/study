package org.liuzhugu.javastudy.framestudy.spring.proxy.dynamicproxy;

public class Test {
    public static void main(String[] args) {
        //用户只接触到代理类
        XiaoMingProxy xiaoMingProxy = new XiaoMingProxy();
        Person proxy = xiaoMingProxy.getProxy();
        proxy.sing(" 我爱你");
    }
}
