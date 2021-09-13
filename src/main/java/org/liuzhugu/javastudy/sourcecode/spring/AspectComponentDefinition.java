package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.BeanReference;

public class AspectComponentDefinition extends CompositeComponentDefinition {
    private final BeanDefinition[] beanDefinitions;
    private final BeanReference[] beanReferences;

    public AspectComponentDefinition(String aspectName, BeanDefinition[] beanDefinitions, BeanReference[] beanReferences, Object source) {
        super(aspectName, source);
        this.beanDefinitions = beanDefinitions != null ? beanDefinitions : new BeanDefinition[0];
        this.beanReferences = beanReferences != null ? beanReferences : new BeanReference[0];
    }

    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
}
