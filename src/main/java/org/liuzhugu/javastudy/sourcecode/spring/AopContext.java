package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.core.NamedThreadLocal;

public abstract class AopContext {
    private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal("Current AOP proxy");

    public AopContext() {
    }

    public static Object currentProxy() throws IllegalStateException {
        Object proxy = currentProxy.get();
        if (proxy == null) {
            throw new IllegalStateException("Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
        } else {
            return proxy;
        }
    }

    static Object setCurrentProxy(Object proxy) {
        Object old = currentProxy.get();
        if (proxy != null) {
            currentProxy.set(proxy);
        } else {
            currentProxy.remove();
        }

        return old;
    }
}