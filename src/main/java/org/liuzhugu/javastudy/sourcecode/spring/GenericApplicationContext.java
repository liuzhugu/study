package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
    private final DefaultListableBeanFactory beanFactory;
    private ResourceLoader resourceLoader;
    private boolean customClassLoader;
    private final AtomicBoolean refreshed;

    public GenericApplicationContext() {
        this.customClassLoader = false;
        this.refreshed = new AtomicBoolean();
        this.beanFactory = new DefaultListableBeanFactory();
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        this.customClassLoader = false;
        this.refreshed = new AtomicBoolean();
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public GenericApplicationContext(ApplicationContext parent) {
        this();
        this.setParent(parent);
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
        this(beanFactory);
        this.setParent(parent);
    }

    public void setParent(ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(this.getInternalParentBeanFactory());
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.beanFactory.setAllowCircularReferences(allowCircularReferences);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getResource(String location) {
        return this.resourceLoader != null ? this.resourceLoader.getResource(location) : super.getResource(location);
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourceLoader instanceof ResourcePatternResolver ? ((ResourcePatternResolver)this.resourceLoader).getResources(locationPattern) : super.getResources(locationPattern);
    }

    public void setClassLoader(ClassLoader classLoader) {
        super.setClassLoader(classLoader);
        this.customClassLoader = true;
    }

    public ClassLoader getClassLoader() {
        return this.resourceLoader != null && !this.customClassLoader ? this.resourceLoader.getClassLoader() : super.getClassLoader();
    }

    protected final void refreshBeanFactory() throws IllegalStateException {
        if (!this.refreshed.compareAndSet(false, true)) {
            throw new IllegalStateException("GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        } else {
            this.beanFactory.setSerializationId(this.getId());
        }
    }

    protected void cancelRefresh(BeansException ex) {
        this.beanFactory.setSerializationId((String)null);
        super.cancelRefresh(ex);
    }

    protected final void closeBeanFactory() {
        this.beanFactory.setSerializationId((String)null);
    }

    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        this.assertBeanFactoryActive();
        return this.beanFactory;
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    public void registerAlias(String beanName, String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }

    public void removeAlias(String alias) {
        this.beanFactory.removeAlias(alias);
    }

    public boolean isAlias(String beanName) {
        return this.beanFactory.isAlias(beanName);
    }
}
