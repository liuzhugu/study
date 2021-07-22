package org.liuzhugu.javastudy.practice.rpc.complex.test.provider.interfaces;

import org.liuzhugu.javastudy.practice.rpc.complex.test.provider.export.HelloService;
import org.liuzhugu.javastudy.practice.rpc.complex.test.provider.export.domain.Hi;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hi() {
        return "hi bugstack rpc";
    }

    @Override
    public String say(String str) {
        return str;
    }

    @Override
    public String sayHi(Hi hi) {
        return hi.getUserName() + " say：" + hi.getSayMsg();
    }
}
