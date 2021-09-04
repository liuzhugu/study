package org.liuzhugu.javastudy.sourcecode.spring;


import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;

public abstract class InstantiationAwareBeanPostProcessorAdapter implements SmartInstantiationAwareBeanPostProcessor {
    public InstantiationAwareBeanPostProcessorAdapter() {
    }

    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        return null;
    }

    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}