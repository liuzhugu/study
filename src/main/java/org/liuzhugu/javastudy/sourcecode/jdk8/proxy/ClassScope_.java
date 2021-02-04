package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.generics.scope.*;


public class ClassScope_ extends AbstractScope_<Class_<?>> implements Scope {
    private ClassScope_(Class_<?> var1) {
        super(var1);
    }

    protected Scope computeEnclosingScope() {
        Class_ var1 = (Class_)this.getRecvr();
        Method_ var2 = var1.getEnclosingMethod();
        if (var2 != null) {
            return MethodScope_.make(var2);
        } else {
            Constructor_ var3 = var1.getEnclosingConstructor();
            if (var3 != null) {
                return ConstructorScope_.make(var3);
            } else {
                Class_ var4 = var1.getEnclosingClass();
                return (Scope)(var4 != null ? make(var4) : DummyScope.make());
            }
        }
    }

    public static ClassScope_ make(Class_<?> var0) {
        return new ClassScope_(var0);
    }
}