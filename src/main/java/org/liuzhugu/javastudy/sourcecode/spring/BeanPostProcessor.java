package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object var1, String var2) throws BeansException;

    Object postProcessAfterInitialization(Object var1, String var2) throws BeansException;
}
