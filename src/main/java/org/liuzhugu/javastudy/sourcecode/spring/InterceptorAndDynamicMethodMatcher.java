package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.MethodMatcher;

class InterceptorAndDynamicMethodMatcher {
    final MethodInterceptor interceptor;
    final MethodMatcher methodMatcher;

    public InterceptorAndDynamicMethodMatcher(MethodInterceptor interceptor, MethodMatcher methodMatcher) {
        this.interceptor = interceptor;
        this.methodMatcher = methodMatcher;
    }
}
