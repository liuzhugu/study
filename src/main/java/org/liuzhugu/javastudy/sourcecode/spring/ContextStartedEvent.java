package org.liuzhugu.javastudy.sourcecode.spring;


public class ContextStartedEvent extends ApplicationContextEvent {
    public ContextStartedEvent(ApplicationContext source) {
        super(source);
    }
}