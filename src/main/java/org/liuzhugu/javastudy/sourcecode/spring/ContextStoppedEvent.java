package org.liuzhugu.javastudy.sourcecode.spring;

public class ContextStoppedEvent extends ApplicationContextEvent {
    public ContextStoppedEvent(ApplicationContext source) {
        super(source);
    }
}
