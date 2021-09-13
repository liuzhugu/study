package org.liuzhugu.javastudy.sourcecode.spring;


public interface AdvisorAdapter {
    boolean supportsAdvice(Advice var1);

    MethodInterceptor getInterceptor(Advisor var1);
}
