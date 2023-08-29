package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;

import java.io.IOException;

public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    private Boolean allowBeanDefinitionOverriding;
    private Boolean allowCircularReferences;
    private DefaultListableBeanFactory beanFactory;
    private final Object beanFactoryMonitor = new Object();

    public AbstractRefreshableApplicationContext() {
    }

    public AbstractRefreshableApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    protected final void refreshBeanFactory() throws BeansException {
        if (this.hasBeanFactory()) {
            this.destroyBeans();
            this.closeBeanFactory();
        }

        try {
            DefaultListableBeanFactory beanFactory = this.createBeanFactory();
            beanFactory.setSerializationId(this.getId());
            this.customizeBeanFactory(beanFactory);
            this.loadBeanDefinitions(beanFactory);
            synchronized(this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        } catch (IOException var5) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + this.getDisplayName(), var5);
        }
    }

    protected void cancelRefresh(BeansException ex) {
        synchronized(this.beanFactoryMonitor) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId((String)null);
            }
        }

        super.cancelRefresh(ex);
    }

    protected final void closeBeanFactory() {
        synchronized(this.beanFactoryMonitor) {
            this.beanFactory.setSerializationId((String)null);
            this.beanFactory = null;
        }
    }

    protected final boolean hasBeanFactory() {
        synchronized(this.beanFactoryMonitor) {
            return this.beanFactory != null;
        }
    }

    public final ConfigurableListableBeanFactory getBeanFactory() {
        synchronized(this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
            } else {
                return this.beanFactory;
            }
        }
    }

    protected void assertBeanFactoryActive() {
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(this.getInternalParentBeanFactory());
    }

    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }

        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }

    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory var1) throws BeansException, IOException;
}

