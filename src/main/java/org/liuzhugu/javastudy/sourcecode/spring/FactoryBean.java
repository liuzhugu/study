package org.liuzhugu.javastudy.sourcecode.spring;

public interface FactoryBean<T> {
    T getObject() throws Exception;

    Class<?> getObjectType();

    boolean isSingleton();
}