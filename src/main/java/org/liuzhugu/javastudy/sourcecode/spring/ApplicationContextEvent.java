package org.liuzhugu.javastudy.sourcecode.spring;



public abstract class ApplicationContextEvent extends ApplicationEvent {
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext)this.getSource();
    }
}