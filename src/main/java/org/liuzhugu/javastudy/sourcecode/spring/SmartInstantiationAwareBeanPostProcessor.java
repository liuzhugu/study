package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import java.lang.reflect.Constructor;

public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    Class<?> predictBeanType(Class<?> var1, String var2) throws BeansException;

    Constructor<?>[] determineCandidateConstructors(Class<?> var1, String var2) throws BeansException;

    Object getEarlyBeanReference(Object var1, String var2) throws BeansException;
}
