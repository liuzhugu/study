package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String var1);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(ResolvableType var1);

    String[] getBeanNamesForType(Class<?> var1);

    String[] getBeanNamesForType(Class<?> var1, boolean var2, boolean var3);

    <T> Map<String, T> getBeansOfType(Class<T> var1) throws BeansException;

    <T> Map<String, T> getBeansOfType(Class<T> var1, boolean var2, boolean var3) throws BeansException;

    String[] getBeanNamesForAnnotation(Class<? extends Annotation> var1);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> var1) throws BeansException;

    <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> var2) throws NoSuchBeanDefinitionException;
}
