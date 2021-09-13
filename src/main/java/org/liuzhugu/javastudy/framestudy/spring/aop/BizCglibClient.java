package org.liuzhugu.javastudy.framestudy.spring.aop;

import net.sf.cglib.proxy.Enhancer;
import org.liuzhugu.javastudy.framestudy.spring.aop.impl.BizInterceptor;

public class BizCglibClient {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Biz.class);
        enhancer.setCallback(new BizInterceptor());
        Biz biz = (Biz) enhancer.create();
        biz.help();
    }
}
