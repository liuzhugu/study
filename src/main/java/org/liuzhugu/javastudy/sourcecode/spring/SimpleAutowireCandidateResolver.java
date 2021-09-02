package org.liuzhugu.javastudy.sourcecode.spring;


public class SimpleAutowireCandidateResolver implements AutowireCandidateResolver {
    public SimpleAutowireCandidateResolver() {
    }

    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    public boolean isRequired(DependencyDescriptor descriptor) {
        return descriptor.isRequired();
    }

    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        return null;
    }

    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
        return null;
    }
}