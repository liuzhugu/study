package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ReplaceOverride extends MethodOverride {
    private final String methodReplacerBeanName;
    private List<String> typeIdentifiers = new LinkedList();

    public ReplaceOverride(String methodName, String methodReplacerBeanName) {
        super(methodName);
        Assert.notNull(methodName, "Method replacer bean name must not be null");
        this.methodReplacerBeanName = methodReplacerBeanName;
    }

    public String getMethodReplacerBeanName() {
        return this.methodReplacerBeanName;
    }

    public void addTypeIdentifier(String identifier) {
        this.typeIdentifiers.add(identifier);
    }

    public boolean matches(Method method) {
        if (!method.getName().equals(this.getMethodName())) {
            return false;
        } else if (!this.isOverloaded()) {
            return true;
        } else if (this.typeIdentifiers.size() != method.getParameterTypes().length) {
            return false;
        } else {
            for(int i = 0; i < this.typeIdentifiers.size(); ++i) {
                String identifier = (String)this.typeIdentifiers.get(i);
                if (!method.getParameterTypes()[i].getName().contains(identifier)) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean equals(Object other) {
        if (other instanceof ReplaceOverride && super.equals(other)) {
            ReplaceOverride that = (ReplaceOverride)other;
            return ObjectUtils.nullSafeEquals(this.methodReplacerBeanName, that.methodReplacerBeanName) && ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.methodReplacerBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
        return hashCode;
    }

    public String toString() {
        return "Replace override for method '" + this.getMethodName() + "'";
    }
}
