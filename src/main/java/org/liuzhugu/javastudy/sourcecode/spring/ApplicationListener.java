package org.liuzhugu.javastudy.sourcecode.spring;


import java.util.EventListener;

public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E var1);
}