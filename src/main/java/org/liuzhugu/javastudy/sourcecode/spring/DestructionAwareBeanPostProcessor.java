package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
    void postProcessBeforeDestruction(Object var1, String var2) throws BeansException;

    boolean requiresDestruction(Object var1);
}