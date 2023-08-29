package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.BeanReference;

public abstract class AbstractComponentDefinition implements ComponentDefinition {
    public AbstractComponentDefinition() {
    }

    public String getDescription() {
        return this.getName();
    }

    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[0];
    }

    public BeanDefinition[] getInnerBeanDefinitions() {
        return new BeanDefinition[0];
    }

    public BeanReference[] getBeanReferences() {
        return new BeanReference[0];
    }

    public String toString() {
        return this.getDescription();
    }
}
