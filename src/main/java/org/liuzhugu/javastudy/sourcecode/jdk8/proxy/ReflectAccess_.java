package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.ConstructorAccessor;
import sun.reflect.MethodAccessor;


class ReflectAccess_ implements LangReflectAccess_ {
    public Field_ newField(Class_<?> declaringClass,
                          String name,
                           Class_<?> type,
                          int modifiers,
                          int slot,
                          String signature,
                          byte[] annotations)
    {
        return new Field_(declaringClass,
                name,
                type,
                modifiers,
                slot,
                signature,
                annotations);
    }

    public Method_ newMethod(Class_<?> declaringClass,
                            String name,
                             Class_<?>[] parameterTypes,
                             Class_<?> returnType,
                             Class_<?>[] checkedExceptions,
                            int modifiers,
                            int slot,
                            String signature,
                            byte[] annotations,
                            byte[] parameterAnnotations,
                            byte[] annotationDefault)
    {
        return new Method_(declaringClass,
                name,
                parameterTypes,
                returnType,
                checkedExceptions,
                modifiers,
                slot,
                signature,
                annotations,
                parameterAnnotations,
                annotationDefault);
    }

    public <T> Constructor_<T> newConstructor(Class_<T> declaringClass,
                                              Class_<?>[] parameterTypes,
                                              Class_<?>[] checkedExceptions,
                                             int modifiers,
                                             int slot,
                                             String signature,
                                             byte[] annotations,
                                             byte[] parameterAnnotations)
    {
        return new Constructor_<>(declaringClass,
                parameterTypes,
                checkedExceptions,
                modifiers,
                slot,
                signature,
                annotations,
                parameterAnnotations);
    }

    public MethodAccessor getMethodAccessor(Method_ m) {
        return m.getMethodAccessor();
    }

    public void setMethodAccessor(Method_ m, MethodAccessor accessor) {
        m.setMethodAccessor(accessor);
    }

    public ConstructorAccessor getConstructorAccessor(Constructor_<?> c) {
        return c.getConstructorAccessor();
    }

    public void setConstructorAccessor(Constructor_<?> c,
                                       ConstructorAccessor accessor)
    {
        c.setConstructorAccessor(accessor);
    }

    public int getConstructorSlot(Constructor_<?> c) {
        return c.getSlot();
    }

    public String getConstructorSignature(Constructor_<?> c) {
        return c.getSignature();
    }

    public byte[] getConstructorAnnotations(Constructor_<?> c) {
        return c.getRawAnnotations();
    }

    public byte[] getConstructorParameterAnnotations(Constructor_<?> c) {
        return c.getRawParameterAnnotations();
    }

    public byte[] getExecutableTypeAnnotationBytes(Executable_ ex) {
        return ex.getTypeAnnotationBytes();
    }

    //
    // Copying routines, needed to quickly fabricate new Field,
    // Method_, and Constructor_ objects from templates
    //
    public Method_      copyMethod(Method_ arg) {
        return arg.copy();
    }

    public Field_       copyField(Field_ arg) {
        return arg.copy();
    }

    public <T> Constructor_<T> copyConstructor(Constructor_<T> arg) {
        return arg.copy();
    }
}