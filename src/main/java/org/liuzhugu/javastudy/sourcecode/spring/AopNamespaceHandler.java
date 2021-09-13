package org.liuzhugu.javastudy.sourcecode.spring;

public class AopNamespaceHandler extends NamespaceHandlerSupport {
    public AopNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
        this.registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
        this.registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());
        this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
    }
}
