package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext var1) throws BeansException;
}
