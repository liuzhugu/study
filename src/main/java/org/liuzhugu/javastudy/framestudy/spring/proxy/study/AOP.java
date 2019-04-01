package org.liuzhugu.javastudy.framestudy.spring.proxy.study;

import org.springframework.stereotype.Component;

@Component
public class AOP {
    public void begin() {
        System.out.println("开启事务");
    }
    public void close() {
        System.out.println("关闭事务");
    }
}
