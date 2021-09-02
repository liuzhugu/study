package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public interface BeanDefinitionReader {
    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    ClassLoader getBeanClassLoader();

    BeanNameGenerator getBeanNameGenerator();

    int loadBeanDefinitions(Resource var1) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(Resource... var1) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String var1) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String... var1) throws BeanDefinitionStoreException;
}
