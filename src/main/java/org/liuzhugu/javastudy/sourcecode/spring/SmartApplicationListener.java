package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.core.Ordered;

public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
    boolean supportsEventType(Class<? extends ApplicationEvent> var1);

    boolean supportsSourceType(Class<?> var1);
}