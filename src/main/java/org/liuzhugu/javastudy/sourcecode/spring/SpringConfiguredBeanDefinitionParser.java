package org.liuzhugu.javastudy.sourcecode.spring;

import org.w3c.dom.Element;

class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {
    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME = "org.springframework.context.config.internalBeanConfigurerAspect";
    private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME = "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

    SpringConfiguredBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.context.config.internalBeanConfigurerAspect")) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName("org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect");
            def.setFactoryMethodName("aspectOf");
            def.setRole(2);
            def.setSource(parserContext.extractSource(element));
            parserContext.registerBeanComponent(new BeanComponentDefinition(def, "org.springframework.context.config.internalBeanConfigurerAspect"));
        }

        return null;
    }
}
