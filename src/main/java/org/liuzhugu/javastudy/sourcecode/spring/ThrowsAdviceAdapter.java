package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.ThrowsAdvice;

import java.io.Serializable;

class ThrowsAdviceAdapter implements AdvisorAdapter, Serializable {
    ThrowsAdviceAdapter() {
    }

    public boolean supportsAdvice(Advice advice) {
        return advice instanceof ThrowsAdvice;
    }

    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new ThrowsAdviceInterceptor(advisor.getAdvice());
    }
}