package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.generics.scope.Scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope_<D extends GenericDeclaration_> implements Scope {
    private final D recvr;
    private volatile Scope enclosingScope;

    protected AbstractScope_(D var1) {
        this.recvr = var1;
    }

    protected D getRecvr() {
        return this.recvr;
    }

    protected abstract Scope computeEnclosingScope();

    protected Scope getEnclosingScope() {
        Scope var1 = this.enclosingScope;
        if (var1 == null) {
            var1 = this.computeEnclosingScope();
            this.enclosingScope = var1;
        }

        return var1;
    }

    @Override
    public TypeVariable<?> lookup(String s) {
        return null;
    }
}