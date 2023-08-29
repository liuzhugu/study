package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry var1) throws BeansException;
}

