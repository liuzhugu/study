package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

public class GenericApplicationListenerAdapter implements GenericApplicationListener, SmartApplicationListener {
    private final ApplicationListener<ApplicationEvent> delegate;
    private final ResolvableType declaredEventType;

    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = (ApplicationListener<ApplicationEvent>) delegate;
        this.declaredEventType = resolveDeclaredEventType(this.delegate);
    }

    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    public boolean supportsEventType(ResolvableType eventType) {
        if (this.delegate instanceof SmartApplicationListener) {
            Class<? extends ApplicationEvent> eventClass = (Class<? extends ApplicationEvent>) eventType.resolve();
            return eventClass != null && ((SmartApplicationListener)this.delegate).supportsEventType(eventClass);
        } else {
            return this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType);
        }
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return this.supportsEventType(ResolvableType.forClass(eventType));
    }

    public boolean supportsSourceType(Class<?> sourceType) {
        return !(this.delegate instanceof SmartApplicationListener) || ((SmartApplicationListener)this.delegate).supportsSourceType(sourceType);
    }

    public int getOrder() {
        return this.delegate instanceof Ordered ? ((Ordered)this.delegate).getOrder() : 2147483647;
    }

    static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
        ResolvableType resolvableType = ResolvableType.forClass(listenerType).as(ApplicationListener.class);
        return resolvableType.hasGenerics() ? resolvableType.getGeneric(new int[0]) : null;
    }

    private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
        ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
        if (declaredEventType == null || declaredEventType.isAssignableFrom(ResolvableType.forClass(ApplicationEvent.class))) {
            Class<?> targetClass = AopUtils.getTargetClass(listener);
            if (targetClass != listener.getClass()) {
                declaredEventType = resolveDeclaredEventType(targetClass);
            }
        }

        return declaredEventType;
    }
}
