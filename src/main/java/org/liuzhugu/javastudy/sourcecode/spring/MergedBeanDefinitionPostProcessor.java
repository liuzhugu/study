package org.liuzhugu.javastudy.sourcecode.spring;


import org.springframework.beans.factory.config.BeanPostProcessor;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
    void postProcessMergedBeanDefinition(RootBeanDefinition var1, Class<?> var2, String var3);
}
