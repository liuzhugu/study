package org.liuzhugu.javastudy.sourcecode.spring;


public interface Advisor {
    Advice getAdvice();

    boolean isPerInstance();
}
