package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor {
    private final Class<?> beanClass;
    private final Method readMethod;
    private final Method writeMethod;
    private volatile Set<Method> ambiguousWriteMethods;
    private MethodParameter writeMethodParameter;
    private Class<?> propertyType;
    private final Class<?> propertyEditorClass;

    public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName, Method readMethod, Method writeMethod, Class<?> propertyEditorClass) throws IntrospectionException {
        super(propertyName, (Method)null, (Method)null);
        if (beanClass == null) {
            throw new IntrospectionException("Bean class must not be null");
        } else {
            this.beanClass = beanClass;
            Method readMethodToUse = BridgeMethodResolver.findBridgedMethod(readMethod);
            Method writeMethodToUse = BridgeMethodResolver.findBridgedMethod(writeMethod);
            if (writeMethodToUse == null && readMethodToUse != null) {
                Method candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + StringUtils.capitalize(this.getName()), (Class[])null);
                if (candidate != null && candidate.getParameterTypes().length == 1) {
                    writeMethodToUse = candidate;
                }
            }

            this.readMethod = readMethodToUse;
            this.writeMethod = writeMethodToUse;
            if (this.writeMethod != null) {
                if (this.readMethod == null) {
                    Set<Method> ambiguousCandidates = new HashSet();
                    Method[] var9 = beanClass.getMethods();
                    int var10 = var9.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        Method method = var9[var11];
                        if (method.getName().equals(writeMethodToUse.getName()) && !method.equals(writeMethodToUse) && !method.isBridge() && method.getParameterTypes().length == writeMethodToUse.getParameterTypes().length) {
                            ambiguousCandidates.add(method);
                        }
                    }

                    if (!ambiguousCandidates.isEmpty()) {
                        this.ambiguousWriteMethods = ambiguousCandidates;
                    }
                }

                this.writeMethodParameter = new MethodParameter(this.writeMethod, 0);
                GenericTypeResolver.resolveParameterType(this.writeMethodParameter, this.beanClass);
            }

            if (this.readMethod != null) {
                this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
            } else if (this.writeMethodParameter != null) {
                this.propertyType = this.writeMethodParameter.getParameterType();
            }

            this.propertyEditorClass = propertyEditorClass;
        }
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Method getWriteMethodForActualAccess() {
        Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
        if (ambiguousCandidates != null) {
            this.ambiguousWriteMethods = null;
            LogFactory.getLog(GenericTypeAwarePropertyDescriptor.class).warn("Invalid JavaBean property '" + this.getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + ambiguousCandidates);
        }

        return this.writeMethod;
    }

    public MethodParameter getWriteMethodParameter() {
        return this.writeMethodParameter;
    }

    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof GenericTypeAwarePropertyDescriptor)) {
            return false;
        } else {
            GenericTypeAwarePropertyDescriptor otherPd = (GenericTypeAwarePropertyDescriptor)other;
            return this.getBeanClass().equals(otherPd.getBeanClass()) && PropertyDescriptorUtils.equals(this, otherPd);
        }
    }

    public int hashCode() {
        int hashCode = this.getBeanClass().hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getReadMethod());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getWriteMethod());
        return hashCode;
    }
}
