package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.AliasRegistry;

public interface BeanDefinitionRegistry extends AliasRegistry {
    void registerBeanDefinition(String var1, BeanDefinition var2) throws BeanDefinitionStoreException;

    void removeBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String var1);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String var1);
}