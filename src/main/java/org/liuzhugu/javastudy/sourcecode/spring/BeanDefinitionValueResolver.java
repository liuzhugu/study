package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

class BeanDefinitionValueResolver {
    private final AbstractBeanFactory beanFactory;
    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final TypeConverter typeConverter;

    public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition, TypeConverter typeConverter) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }

    public Object resolveValueIfNecessary(Object argName, Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference)value;
            return this.resolveReference(argName, ref);
        } else if (value instanceof RuntimeBeanNameReference) {
            String refName = ((RuntimeBeanNameReference)value).getBeanName();
            refName = String.valueOf(this.doEvaluate(refName));
            if (!this.beanFactory.containsBean(refName)) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            } else {
                return refName;
            }
        } else if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bdHolder = (BeanDefinitionHolder)value;
            return this.resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
        } else if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition)value;
            String innerBeanName = "(inner bean)#" + ObjectUtils.getIdentityHexString(bd);
            return this.resolveInnerBean(argName, innerBeanName, bd);
        } else if (value instanceof ManagedArray) {
            ManagedArray array = (ManagedArray)value;
            Class<?> elementType = array.resolvedElementType;
            if (elementType == null) {
                String elementTypeName = array.getElementTypeName();
                if (StringUtils.hasText(elementTypeName)) {
                    try {
                        elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
                        array.resolvedElementType = elementType;
                    } catch (Throwable var9) {
                        throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error resolving array type for " + argName, var9);
                    }
                } else {
                    elementType = Object.class;
                }
            }

            return this.resolveManagedArray(argName, (List)value, elementType);
        } else if (value instanceof ManagedList) {
            return this.resolveManagedList(argName, (List)value);
        } else if (value instanceof ManagedSet) {
            return this.resolveManagedSet(argName, (Set)value);
        } else if (value instanceof ManagedMap) {
            return this.resolveManagedMap(argName, (Map)value);
        } else if (value instanceof ManagedProperties) {
            Properties original = (Properties)value;
            Properties copy = new Properties();

            Object propKey;
            Object propValue;
            for(Iterator var19 = original.entrySet().iterator(); var19.hasNext(); copy.put(propKey, propValue)) {
                Map.Entry<Object, Object> propEntry = (Map.Entry)var19.next();
                propKey = propEntry.getKey();
                propValue = propEntry.getValue();
                if (propKey instanceof TypedStringValue) {
                    propKey = this.evaluate((TypedStringValue)propKey);
                }

                if (propValue instanceof TypedStringValue) {
                    propValue = this.evaluate((TypedStringValue)propValue);
                }
            }

            return copy;
        } else if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue)value;
            Object valueObject = this.evaluate(typedStringValue);

            try {
                Class<?> resolvedTargetType = this.resolveTargetType(typedStringValue);
                return resolvedTargetType != null ? this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType) : valueObject;
            } catch (Throwable var10) {
                throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, var10);
            }
        } else {
            return this.evaluate(value);
        }
    }

    protected Object evaluate(TypedStringValue value) {
        Object result = this.doEvaluate(value.getValue());
        if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
            value.setDynamic();
        }

        return result;
    }

    protected Object evaluate(Object value) {
        if (value instanceof String) {
            return this.doEvaluate((String)value);
        } else if (value instanceof String[]) {
            String[] values = (String[])((String[])value);
            boolean actuallyResolved = false;
            Object[] resolvedValues = new Object[values.length];

            for(int i = 0; i < values.length; ++i) {
                String originalValue = values[i];
                Object resolvedValue = this.doEvaluate(originalValue);
                if (resolvedValue != originalValue) {
                    actuallyResolved = true;
                }

                resolvedValues[i] = resolvedValue;
            }

            return actuallyResolved ? resolvedValues : values;
        } else {
            return value;
        }
    }

    private Object doEvaluate(String value) {
        return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
    }

    protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
        return value.hasTargetType() ? value.getTargetType() : value.resolveTargetType(this.beanFactory.getBeanClassLoader());
    }

    private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
        RootBeanDefinition mbd = null;

        try {
            mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
            String actualInnerBeanName = innerBeanName;
            if (mbd.isSingleton()) {
                actualInnerBeanName = this.adaptInnerBeanName(innerBeanName);
            }

            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                String[] var7 = dependsOn;
                int var8 = dependsOn.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    String dependsOnBean = var7[var9];
                    this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
                    this.beanFactory.getBean(dependsOnBean);
                }
            }

            Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, (Object[])null);
            if (innerBean instanceof FactoryBean) {
                boolean synthetic = mbd.isSynthetic();
                return this.beanFactory.getObjectFromFactoryBean((FactoryBean)innerBean, actualInnerBeanName, !synthetic);
            } else {
                return innerBean;
            }
        } catch (BeansException var11) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + (mbd != null && mbd.getBeanClassName() != null ? "of type [" + mbd.getBeanClassName() + "] " : "") + "while setting " + argName, var11);
        }
    }

    private String adaptInnerBeanName(String innerBeanName) {
        String actualInnerBeanName = innerBeanName;

        for(int counter = 0; this.beanFactory.isBeanNameInUse(actualInnerBeanName); actualInnerBeanName = innerBeanName + "#" + counter) {
            ++counter;
        }

        return actualInnerBeanName;
    }

    private Object resolveReference(Object argName, RuntimeBeanReference ref) {
        try {
            String refName = ref.getBeanName();
            refName = String.valueOf(this.doEvaluate(refName));
            if (ref.isToParent()) {
                if (this.beanFactory.getParentBeanFactory() == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Can't resolve reference to bean '" + refName + "' in parent factory: no parent factory available");
                } else {
                    return this.beanFactory.getParentBeanFactory().getBean(refName);
                }
            } else {
                Object bean = this.beanFactory.getBean(refName);
                this.beanFactory.registerDependentBean(refName, this.beanName);
                return bean;
            }
        } catch (BeansException var5) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, var5);
        }
    }

    private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
        Object resolved = Array.newInstance(elementType, ml.size());

        for(int i = 0; i < ml.size(); ++i) {
            Array.set(resolved, i, this.resolveValueIfNecessary(new BeanDefinitionValueResolver.KeyedArgName(argName, i), ml.get(i)));
        }

        return resolved;
    }

    private List<?> resolveManagedList(Object argName, List<?> ml) {
        List<Object> resolved = new ArrayList(ml.size());

        for(int i = 0; i < ml.size(); ++i) {
            resolved.add(this.resolveValueIfNecessary(new BeanDefinitionValueResolver.KeyedArgName(argName, i), ml.get(i)));
        }

        return resolved;
    }

    private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
        Set<Object> resolved = new LinkedHashSet(ms.size());
        int i = 0;

        for(Iterator var5 = ms.iterator(); var5.hasNext(); ++i) {
            Object m = var5.next();
            resolved.add(this.resolveValueIfNecessary(new BeanDefinitionValueResolver.KeyedArgName(argName, i), m));
        }

        return resolved;
    }

    private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
        Map<Object, Object> resolved = new LinkedHashMap(mm.size());
        Iterator var4 = mm.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)var4.next();
            Object resolvedKey = this.resolveValueIfNecessary(argName, entry.getKey());
            Object resolvedValue = this.resolveValueIfNecessary(new BeanDefinitionValueResolver.KeyedArgName(argName, entry.getKey()), entry.getValue());
            resolved.put(resolvedKey, resolvedValue);
        }

        return resolved;
    }

    private static class KeyedArgName {
        private final Object argName;
        private final Object key;

        public KeyedArgName(Object argName, Object key) {
            this.argName = argName;
            this.key = key;
        }

        public String toString() {
            return this.argName + " with key " + "[" + this.key + "]";
        }
    }
}
