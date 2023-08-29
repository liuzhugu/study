package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory var1) throws BeansException;
}
