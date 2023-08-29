package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.util.ObjectUtils;

import java.io.Serializable;

public class EmptyTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 3680494563553489691L;
    public static final EmptyTargetSource INSTANCE = new EmptyTargetSource((Class)null, true);
    private final Class<?> targetClass;
    private final boolean isStatic;

    public static EmptyTargetSource forClass(Class<?> targetClass) {
        return forClass(targetClass, true);
    }

    public static EmptyTargetSource forClass(Class<?> targetClass, boolean isStatic) {
        return targetClass == null && isStatic ? INSTANCE : new EmptyTargetSource(targetClass, isStatic);
    }

    private EmptyTargetSource(Class<?> targetClass, boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public Object getTarget() {
        return null;
    }

    public void releaseTarget(Object target) {
    }

    private Object readResolve() {
        return this.targetClass == null && this.isStatic ? INSTANCE : this;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof EmptyTargetSource)) {
            return false;
        } else {
            EmptyTargetSource otherTs = (EmptyTargetSource)other;
            return ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic;
        }
    }

    public int hashCode() {
        return EmptyTargetSource.class.hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetClass);
    }

    public String toString() {
        return "EmptyTargetSource: " + (this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") + ", " + (this.isStatic ? "static" : "dynamic");
    }
}
