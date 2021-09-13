package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.framework.*;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ProxyCreatorSupport extends AdvisedSupport {
    private AopProxyFactory aopProxyFactory;
    private List<AdvisedSupportListener> listeners = new LinkedList();
    private boolean active = false;

    public ProxyCreatorSupport() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    public ProxyCreatorSupport(AopProxyFactory aopProxyFactory) {
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void addListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.add(listener);
    }

    public void removeListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.remove(listener);
    }

    protected final synchronized AopProxy createAopProxy() {
        if (!this.active) {
            this.activate();
        }

        return this.getAopProxyFactory().createAopProxy(this);
    }

    private void activate() {
        this.active = true;
        Iterator var1 = this.listeners.iterator();

        while(var1.hasNext()) {
            AdvisedSupportListener listener = (AdvisedSupportListener)var1.next();
            listener.activated(this);
        }

    }

    protected void adviceChanged() {
        super.adviceChanged();
        synchronized(this) {
            if (this.active) {
                Iterator var2 = this.listeners.iterator();

                while(var2.hasNext()) {
                    AdvisedSupportListener listener = (AdvisedSupportListener)var2.next();
                    listener.adviceChanged(this);
                }
            }

        }
    }

    protected final synchronized boolean isActive() {
        return this.active;
    }
}