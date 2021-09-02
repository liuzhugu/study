package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;

public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
    boolean supportsEventType(ResolvableType var1);

    boolean supportsSourceType(Class<?> var1);
}