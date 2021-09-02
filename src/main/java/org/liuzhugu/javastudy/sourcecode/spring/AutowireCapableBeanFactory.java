package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.NamedBeanHolder;

import java.util.Set;

public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;
    int AUTOWIRE_CONSTRUCTOR = 3;
    /** @deprecated */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    <T> T createBean(Class<T> var1) throws BeansException;

    void autowireBean(Object var1) throws BeansException;

    Object configureBean(Object var1, String var2) throws BeansException;

    Object createBean(Class<?> var1, int var2, boolean var3) throws BeansException;

    Object autowire(Class<?> var1, int var2, boolean var3) throws BeansException;

    void autowireBeanProperties(Object var1, int var2, boolean var3) throws BeansException;

    void applyBeanPropertyValues(Object var1, String var2) throws BeansException;

    Object initializeBean(Object var1, String var2) throws BeansException;

    Object applyBeanPostProcessorsBeforeInitialization(Object var1, String var2) throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object var1, String var2) throws BeansException;

    void destroyBean(Object var1);

    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> var1) throws BeansException;

    Object resolveDependency(DependencyDescriptor var1, String var2) throws BeansException;

    Object resolveDependency(DependencyDescriptor var1, String var2, Set<String> var3, TypeConverter var4) throws BeansException;
}
