package org.liuzhugu.javastudy.sourcecode.spring;



public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
    void postProcessMergedBeanDefinition(RootBeanDefinition var1, Class<?> var2, String var3);
}
