package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.generics.scope.AbstractScope;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.scope.Scope;

import java.lang.reflect.Method;

public class MethodScope_ extends AbstractScope_<Method_> {
    private MethodScope_(Method_ var1) {
        super(var1);
    }

    private Class_<?> getEnclosingClass() {
        return ((Method_)this.getRecvr()).getDeclaringClass();
    }

    protected Scope computeEnclosingScope() {
        return ClassScope_.make(this.getEnclosingClass());
    }

    public static MethodScope_ make(Method_ var0) {
        return new MethodScope_(var0);
    }
}
