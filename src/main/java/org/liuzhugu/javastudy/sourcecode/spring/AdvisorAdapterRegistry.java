package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;

public interface AdvisorAdapterRegistry {
    Advisor wrap(Object var1) throws UnknownAdviceTypeException;

    MethodInterceptor[] getInterceptors(Advisor var1) throws UnknownAdviceTypeException;

    void registerAdvisorAdapter(AdvisorAdapter var1);
}
