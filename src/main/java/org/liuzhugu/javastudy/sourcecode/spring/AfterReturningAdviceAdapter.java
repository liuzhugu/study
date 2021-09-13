package org.liuzhugu.javastudy.sourcecode.spring;


import java.io.Serializable;

class AfterReturningAdviceAdapter implements AdvisorAdapter, Serializable {
    AfterReturningAdviceAdapter() {
    }

    public boolean supportsAdvice(Advice advice) {
        return advice instanceof AfterReturningAdvice;
    }

    public MethodInterceptor getInterceptor(Advisor advisor) {
        AfterReturningAdvice advice = (AfterReturningAdvice)advisor.getAdvice();
        return new AfterReturningAdviceInterceptor(advice);
    }
}
