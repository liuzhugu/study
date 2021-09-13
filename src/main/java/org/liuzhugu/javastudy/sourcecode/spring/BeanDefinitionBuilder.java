package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.ObjectUtils;

public class BeanDefinitionBuilder {
    private AbstractBeanDefinition beanDefinition;
    private int constructorArgIndex;

    public static BeanDefinitionBuilder genericBeanDefinition() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new GenericBeanDefinition();
        return builder;
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new GenericBeanDefinition();
        builder.beanDefinition.setBeanClass(beanClass);
        return builder;
    }

    public static BeanDefinitionBuilder genericBeanDefinition(String beanClassName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new GenericBeanDefinition();
        builder.beanDefinition.setBeanClassName(beanClassName);
        return builder;
    }

    public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass) {
        return rootBeanDefinition((Class)beanClass, (String)null);
    }

    public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass, String factoryMethodName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new RootBeanDefinition();
        builder.beanDefinition.setBeanClass(beanClass);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }

    public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName) {
        return rootBeanDefinition((String)beanClassName, (String)null);
    }

    public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName, String factoryMethodName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new RootBeanDefinition();
        builder.beanDefinition.setBeanClassName(beanClassName);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }

    public static BeanDefinitionBuilder childBeanDefinition(String parentName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new ChildBeanDefinition(parentName);
        return builder;
    }

    private BeanDefinitionBuilder() {
    }

    public AbstractBeanDefinition getRawBeanDefinition() {
        return this.beanDefinition;
    }

    public AbstractBeanDefinition getBeanDefinition() {
        this.beanDefinition.validate();
        return this.beanDefinition;
    }

    public BeanDefinitionBuilder setParentName(String parentName) {
        this.beanDefinition.setParentName(parentName);
        return this;
    }

    public BeanDefinitionBuilder setFactoryMethod(String factoryMethod) {
        this.beanDefinition.setFactoryMethodName(factoryMethod);
        return this;
    }

    public BeanDefinitionBuilder setFactoryMethodOnBean(String factoryMethod, String factoryBean) {
        this.beanDefinition.setFactoryMethodName(factoryMethod);
        this.beanDefinition.setFactoryBeanName(factoryBean);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public BeanDefinitionBuilder addConstructorArg(Object value) {
        return this.addConstructorArgValue(value);
    }

    public BeanDefinitionBuilder addConstructorArgValue(Object value) {
        this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, value);
        return this;
    }

    public BeanDefinitionBuilder addConstructorArgReference(String beanName) {
        this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, new RuntimeBeanReference(beanName));
        return this;
    }

    public BeanDefinitionBuilder addPropertyValue(String name, Object value) {
        this.beanDefinition.getPropertyValues().add(name, value);
        return this;
    }

    public BeanDefinitionBuilder addPropertyReference(String name, String beanName) {
        this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
        return this;
    }

    public BeanDefinitionBuilder setInitMethodName(String methodName) {
        this.beanDefinition.setInitMethodName(methodName);
        return this;
    }

    public BeanDefinitionBuilder setDestroyMethodName(String methodName) {
        this.beanDefinition.setDestroyMethodName(methodName);
        return this;
    }

    public BeanDefinitionBuilder setScope(String scope) {
        this.beanDefinition.setScope(scope);
        return this;
    }

    public BeanDefinitionBuilder setAbstract(boolean flag) {
        this.beanDefinition.setAbstract(flag);
        return this;
    }

    public BeanDefinitionBuilder setLazyInit(boolean lazy) {
        this.beanDefinition.setLazyInit(lazy);
        return this;
    }

    public BeanDefinitionBuilder setAutowireMode(int autowireMode) {
        this.beanDefinition.setAutowireMode(autowireMode);
        return this;
    }

    public BeanDefinitionBuilder setDependencyCheck(int dependencyCheck) {
        this.beanDefinition.setDependencyCheck(dependencyCheck);
        return this;
    }

    public BeanDefinitionBuilder addDependsOn(String beanName) {
        if (this.beanDefinition.getDependsOn() == null) {
            this.beanDefinition.setDependsOn(new String[]{beanName});
        } else {
            String[] added = (String[]) ObjectUtils.addObjectToArray(this.beanDefinition.getDependsOn(), beanName);
            this.beanDefinition.setDependsOn(added);
        }

        return this;
    }

    public BeanDefinitionBuilder setRole(int role) {
        this.beanDefinition.setRole(role);
        return this;
    }
}