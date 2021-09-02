package org.liuzhugu.javastudy.sourcecode.spring;


public interface AutowireCandidateResolver {
    boolean isAutowireCandidate(BeanDefinitionHolder var1, DependencyDescriptor var2);

    Object getSuggestedValue(DependencyDescriptor var1);

    Object getLazyResolutionProxyIfNecessary(DependencyDescriptor var1, String var2);
}
