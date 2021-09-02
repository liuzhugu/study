package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.Scope;
import org.springframework.util.Assert;

public class BeanExpressionContext {
    private final ConfigurableBeanFactory beanFactory;
    private final Scope scope;

    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public final Scope getScope() {
        return this.scope;
    }

    public boolean containsObject(String key) {
        return this.beanFactory.containsBean(key) || this.scope != null && this.scope.resolveContextualObject(key) != null;
    }

    public Object getObject(String key) {
        if (this.beanFactory.containsBean(key)) {
            return this.beanFactory.getBean(key);
        } else {
            return this.scope != null ? this.scope.resolveContextualObject(key) : null;
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof BeanExpressionContext)) {
            return false;
        } else {
            BeanExpressionContext otherContext = (BeanExpressionContext)other;
            return this.beanFactory == otherContext.beanFactory && this.scope == otherContext.scope;
        }
    }

    public int hashCode() {
        return this.beanFactory.hashCode();
    }
}