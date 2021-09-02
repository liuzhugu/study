package org.liuzhugu.javastudy.sourcecode.spring;


public class DefaultBeanNameGenerator implements BeanNameGenerator {
    public DefaultBeanNameGenerator() {
    }

    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
    }
}
