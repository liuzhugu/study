package org.liuzhugu.javastudy.sourcecode.spring;


public class ContextClosedEvent extends ApplicationContextEvent {
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}
