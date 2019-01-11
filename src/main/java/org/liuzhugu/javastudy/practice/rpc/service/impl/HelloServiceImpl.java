package org.liuzhugu.javastudy.practice.rpc.service.impl;

import org.liuzhugu.javastudy.practice.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "hello "+name+"!";
    }
}
