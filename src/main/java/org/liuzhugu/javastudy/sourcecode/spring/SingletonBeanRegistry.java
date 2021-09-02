package org.liuzhugu.javastudy.sourcecode.spring;

public interface SingletonBeanRegistry {
    void registerSingleton(String var1, Object var2);

    Object getSingleton(String var1);

    boolean containsSingleton(String var1);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}