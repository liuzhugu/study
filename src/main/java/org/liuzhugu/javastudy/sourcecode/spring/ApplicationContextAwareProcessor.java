package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.context.*;
import org.springframework.util.StringValueResolver;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;
    private final StringValueResolver embeddedValueResolver;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }

    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        AccessControlContext acc = null;
        if (System.getSecurityManager() != null && (bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware || bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware || bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }

        if (acc != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    ApplicationContextAwareProcessor.this.invokeAwareInterfaces(bean);
                    return null;
                }
            }, acc);
        } else {
            this.invokeAwareInterfaces(bean);
        }

        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware)bean).setEnvironment(this.applicationContext.getEnvironment());
            }

            if (bean instanceof EmbeddedValueResolverAware) {
                ((EmbeddedValueResolverAware)bean).setEmbeddedValueResolver(this.embeddedValueResolver);
            }

            if (bean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)bean).setResourceLoader(this.applicationContext);
            }

            if (bean instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware)bean).setApplicationEventPublisher(this.applicationContext);
            }

            if (bean instanceof MessageSourceAware) {
                ((MessageSourceAware)bean).setMessageSource(this.applicationContext);
            }

            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware)bean).setApplicationContext(this.applicationContext);
            }
        }

    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}