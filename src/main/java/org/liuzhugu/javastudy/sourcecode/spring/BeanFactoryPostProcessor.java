package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory var1) throws BeansException;
}
