package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class RootBeanDefinition extends AbstractBeanDefinition {
    private BeanDefinitionHolder decoratedDefinition;
    private AnnotatedElement qualifiedElement;
    boolean allowCaching = true;
    boolean isFactoryMethodUnique = false;
    volatile ResolvableType targetType;
    volatile Class<?> resolvedTargetType;
    volatile ResolvableType factoryMethodReturnType;
    final Object constructorArgumentLock = new Object();
    Object resolvedConstructorOrFactoryMethod;
    boolean constructorArgumentsResolved = false;
    Object[] resolvedConstructorArguments;
    Object[] preparedConstructorArguments;
    final Object postProcessingLock = new Object();
    boolean postProcessed = false;
    volatile Boolean beforeInstantiationResolved;
    private Set<Member> externallyManagedConfigMembers;
    private Set<String> externallyManagedInitMethods;
    private Set<String> externallyManagedDestroyMethods;

    public RootBeanDefinition() {
    }

    public RootBeanDefinition(Class<?> beanClass) {
        this.setBeanClass(beanClass);
    }

    public RootBeanDefinition(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        this.setBeanClass(beanClass);
        this.setAutowireMode(autowireMode);
        if (dependencyCheck && this.getResolvedAutowireMode() != 3) {
            this.setDependencyCheck(1);
        }

    }

    public RootBeanDefinition(Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.setBeanClass(beanClass);
    }

    public RootBeanDefinition(String beanClassName) {
        this.setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
    }

    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public String getParentName() {
        return null;
    }

    public void setParentName(String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    public void setDecoratedDefinition(BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }

    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }

    public void setQualifiedElement(AnnotatedElement qualifiedElement) {
        this.qualifiedElement = qualifiedElement;
    }

    public AnnotatedElement getQualifiedElement() {
        return this.qualifiedElement;
    }

    public void setTargetType(ResolvableType targetType) {
        this.targetType = targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType != null ? ResolvableType.forClass(targetType) : null;
    }

    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        } else {
            return this.targetType != null ? this.targetType.resolve() : null;
        }
    }

    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        this.setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate != null && candidate.getName().equals(this.getFactoryMethodName());
    }

    public Method getResolvedFactoryMethod() {
        synchronized(this.constructorArgumentLock) {
            Object candidate = this.resolvedConstructorOrFactoryMethod;
            return candidate instanceof Method ? (Method)candidate : null;
        }
    }

    public void registerExternallyManagedConfigMember(Member configMember) {
        synchronized(this.postProcessingLock) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new HashSet(1);
            }

            this.externallyManagedConfigMembers.add(configMember);
        }
    }

    public boolean isExternallyManagedConfigMember(Member configMember) {
        synchronized(this.postProcessingLock) {
            return this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers.contains(configMember);
        }
    }

    public void registerExternallyManagedInitMethod(String initMethod) {
        synchronized(this.postProcessingLock) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new HashSet(1);
            }

            this.externallyManagedInitMethods.add(initMethod);
        }
    }

    public boolean isExternallyManagedInitMethod(String initMethod) {
        synchronized(this.postProcessingLock) {
            return this.externallyManagedInitMethods != null && this.externallyManagedInitMethods.contains(initMethod);
        }
    }

    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized(this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new HashSet(1);
            }

            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }

    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized(this.postProcessingLock) {
            return this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods.contains(destroyMethod);
        }
    }

    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    public boolean equals(Object other) {
        return this == other || other instanceof RootBeanDefinition && super.equals(other);
    }

    public String toString() {
        return "Root bean: " + super.toString();
    }
}