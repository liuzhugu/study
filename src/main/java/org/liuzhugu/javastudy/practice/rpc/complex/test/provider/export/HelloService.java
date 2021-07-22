package org.liuzhugu.javastudy.practice.rpc.complex.test.provider.export;

import org.liuzhugu.javastudy.practice.rpc.complex.test.provider.export.domain.Hi;

public interface HelloService {
    String hi();

    String say(String str);

    String sayHi(Hi hi);
}
