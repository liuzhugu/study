package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.Aware;

public interface ApplicationEventPublisherAware extends Aware {
    void setApplicationEventPublisher(ApplicationEventPublisher var1);
}