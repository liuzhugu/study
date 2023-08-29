package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.core.ResolvableType;

public interface ApplicationEventMulticaster {
    void addApplicationListener(ApplicationListener<?> var1);

    void addApplicationListenerBean(String var1);

    void removeApplicationListener(ApplicationListener<?> var1);

    void removeApplicationListenerBean(String var1);

    void removeAllListeners();

    void multicastEvent(ApplicationEvent var1);

    void multicastEvent(ApplicationEvent var1, ResolvableType var2);
}