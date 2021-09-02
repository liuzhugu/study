package org.liuzhugu.javastudy.sourcecode.spring;


public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent var1);

    void publishEvent(Object var1);
}

