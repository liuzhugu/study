package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface InstantiationStrategy {
    Object instantiate(RootBeanDefinition var1, String var2, BeanFactory var3) throws BeansException;

    Object instantiate(RootBeanDefinition var1, String var2, BeanFactory var3, Constructor<?> var4, Object... var5) throws BeansException;

    Object instantiate(RootBeanDefinition var1, String var2, BeanFactory var3, Object var4, Method var5, Object... var6) throws BeansException;
}