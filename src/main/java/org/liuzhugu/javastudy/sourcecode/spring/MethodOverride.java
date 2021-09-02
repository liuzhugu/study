package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

public abstract class MethodOverride implements BeanMetadataElement {
    private final String methodName;
    private boolean overloaded = true;
    private Object source;

    protected MethodOverride(String methodName) {
        Assert.notNull(methodName, "Method name must not be null");
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    protected void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected boolean isOverloaded() {
        return this.overloaded;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }

    public abstract boolean matches(Method var1);

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof MethodOverride)) {
            return false;
        } else {
            MethodOverride that = (MethodOverride)other;
            return ObjectUtils.nullSafeEquals(this.methodName, that.methodName) && ObjectUtils.nullSafeEquals(this.source, that.source);
        }
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.methodName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.source);
        return hashCode;
    }
}
