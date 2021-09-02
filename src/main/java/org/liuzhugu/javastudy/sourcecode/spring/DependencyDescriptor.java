package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class DependencyDescriptor extends InjectionPoint implements Serializable {
    private final Class<?> declaringClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private int parameterIndex;
    private String fieldName;
    private final boolean required;
    private final boolean eager;
    private int nestingLevel;
    private Class<?> containingClass;
    private volatile ResolvableType resolvableType;

    public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
        this(methodParameter, required, true);
    }

    public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
        super(methodParameter);
        this.nestingLevel = 1;
        this.declaringClass = methodParameter.getDeclaringClass();
        if (this.methodParameter.getMethod() != null) {
            this.methodName = methodParameter.getMethod().getName();
            this.parameterTypes = methodParameter.getMethod().getParameterTypes();
        } else {
            this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
        }

        this.parameterIndex = methodParameter.getParameterIndex();
        this.containingClass = methodParameter.getContainingClass();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(Field field, boolean required) {
        this(field, required, true);
    }

    public DependencyDescriptor(Field field, boolean required, boolean eager) {
        super(field);
        this.nestingLevel = 1;
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(DependencyDescriptor original) {
        super(original);
        this.nestingLevel = 1;
        this.declaringClass = original.declaringClass;
        this.methodName = original.methodName;
        this.parameterTypes = original.parameterTypes;
        this.parameterIndex = original.parameterIndex;
        this.fieldName = original.fieldName;
        this.containingClass = original.containingClass;
        this.required = original.required;
        this.eager = original.eager;
        this.nestingLevel = original.nestingLevel;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isEager() {
        return this.eager;
    }

    public Object resolveNotUnique(Class<?> type, Map<String, Object> matchingBeans) throws BeansException {
        throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
    }

    public Object resolveShortcut(BeanFactory beanFactory) throws BeansException {
        return null;
    }

    public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) throws BeansException {
        return beanFactory.getBean(beanName, requiredType);
    }

    public void increaseNestingLevel() {
        ++this.nestingLevel;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter.increaseNestingLevel();
        }

    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            GenericTypeResolver.resolveParameterType(this.methodParameter, containingClass);
        }

    }

    public ResolvableType getResolvableType() {
        ResolvableType resolvableType = this.resolvableType;
        if (resolvableType == null) {
            resolvableType = this.field != null ? ResolvableType.forField(this.field, this.nestingLevel, this.containingClass) : ResolvableType.forMethodParameter(this.methodParameter);
            this.resolvableType = resolvableType;
        }

        return resolvableType;
    }

    public boolean fallbackMatchAllowed() {
        return false;
    }

    public DependencyDescriptor forFallbackMatch() {
        return new DependencyDescriptor(this) {
            public boolean fallbackMatchAllowed() {
                return true;
            }
        };
    }

    public void initParameterNameDiscovery(ParameterNameDiscoverer parameterNameDiscoverer) {
        if (this.methodParameter != null) {
            this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
        }

    }

    public String getDependencyName() {
        return this.field != null ? this.field.getName() : this.methodParameter.getParameterName();
    }

    public Class<?> getDependencyType() {
        if (this.field != null) {
            if (this.nestingLevel > 1) {
                Type type = this.field.getGenericType();

                for(int i = 2; i <= this.nestingLevel; ++i) {
                    if (type instanceof ParameterizedType) {
                        Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                        type = args[args.length - 1];
                    }
                }

                if (type instanceof Class) {
                    return (Class)type;
                } else {
                    if (type instanceof ParameterizedType) {
                        Type arg = ((ParameterizedType)type).getRawType();
                        if (arg instanceof Class) {
                            return (Class)arg;
                        }
                    }

                    return Object.class;
                }
            } else {
                return this.field.getType();
            }
        } else {
            return this.methodParameter.getNestedParameterType();
        }
    }

    /** @deprecated */
    @Deprecated
    public Class<?> getCollectionType() {
        return this.field != null ? GenericCollectionTypeResolver.getCollectionFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getCollectionParameterType(this.methodParameter);
    }

    /** @deprecated */
    @Deprecated
    public Class<?> getMapKeyType() {
        return this.field != null ? GenericCollectionTypeResolver.getMapKeyFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getMapKeyParameterType(this.methodParameter);
    }

    /** @deprecated */
    @Deprecated
    public Class<?> getMapValueType() {
        return this.field != null ? GenericCollectionTypeResolver.getMapValueFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getMapValueParameterType(this.methodParameter);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!super.equals(other)) {
            return false;
        } else {
            DependencyDescriptor otherDesc = (DependencyDescriptor)other;
            return this.required == otherDesc.required && this.eager == otherDesc.eager && this.nestingLevel == otherDesc.nestingLevel && this.containingClass == otherDesc.containingClass;
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

        try {
            if (this.fieldName != null) {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            } else {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                } else {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }

                for(int i = 1; i < this.nestingLevel; ++i) {
                    this.methodParameter.increaseNestingLevel();
                }
            }

        } catch (Throwable var3) {
            throw new IllegalStateException("Could not find original class structure", var3);
        }
    }
}
