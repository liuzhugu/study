package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.generics.scope.AbstractScope;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.scope.Scope;

import java.lang.reflect.Constructor;

public class ConstructorScope_ extends AbstractScope_<Constructor_<?>> {
    private ConstructorScope_(Constructor_<?> var1) {
        super(var1);
    }

    private Class_<?> getEnclosingClass() {
        return ((Constructor_)this.getRecvr()).getDeclaringClass();
    }

    protected Scope computeEnclosingScope() {
        return ClassScope_.make(this.getEnclosingClass());
    }

    public static ConstructorScope_ make(Constructor_<?> var0) {
        return new ConstructorScope_(var0);
    }
}
