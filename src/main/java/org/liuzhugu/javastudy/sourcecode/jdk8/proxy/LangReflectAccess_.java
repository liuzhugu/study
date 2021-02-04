package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.ConstructorAccessor;
import sun.reflect.MethodAccessor;

public interface LangReflectAccess_ {
    Field_ newField(Class_<?> var1, String var2, Class_<?> var3, int var4, int var5, String var6, byte[] var7);

    Method_ newMethod(Class_<?> var1, String var2, Class_<?>[] var3, Class_<?> var4, Class_<?>[] var5, int var6, int var7, String var8, byte[] var9, byte[] var10, byte[] var11);

    <T> Constructor_<T> newConstructor(Class_<T> var1, Class_<?>[] var2, Class_<?>[] var3, int var4, int var5, String var6, byte[] var7, byte[] var8);

    MethodAccessor getMethodAccessor(Method_ var1);

    void setMethodAccessor(Method_ var1, MethodAccessor var2);

    ConstructorAccessor getConstructorAccessor(Constructor_<?> var1);

    void setConstructorAccessor(Constructor_<?> var1, ConstructorAccessor var2);

    byte[] getExecutableTypeAnnotationBytes(Executable_ var1);

    int getConstructorSlot(Constructor_<?> var1);

    String getConstructorSignature(Constructor_<?> var1);

    byte[] getConstructorAnnotations(Constructor_<?> var1);

    byte[] getConstructorParameterAnnotations(Constructor_<?> var1);

    Method_ copyMethod(Method_ var1);

    Field_ copyField(Field_ var1);

    <T> Constructor_<T> copyConstructor(Constructor_<T> var1);
}
