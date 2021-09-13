package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal();

    public SimpleInstantiationStrategy() {
    }

    public static Method getCurrentlyInvokedFactoryMethod() {
        return (Method)currentlyInvokedFactoryMethod.get();
    }

    public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner) {
        if (bd.getMethodOverrides().isEmpty()) {
            Constructor constructorToUse;
            synchronized(bd.constructorArgumentLock) {
                constructorToUse = (Constructor)bd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse == null) {
                    final Class<?> clazz = bd.getBeanClass();
                    if (clazz.isInterface()) {
                        throw new BeanInstantiationException(clazz, "Specified class is an interface");
                    }

                    try {
                        if (System.getSecurityManager() != null) {
                            constructorToUse = (Constructor) AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                                public Constructor<?> run() throws Exception {
                                    return clazz.getDeclaredConstructor((Class[])null);
                                }
                            });
                        } else {
                            constructorToUse = clazz.getDeclaredConstructor((Class[])null);
                        }

                        bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                    } catch (Throwable var9) {
                        throw new BeanInstantiationException(clazz, "No default constructor found", var9);
                    }
                }
            }

            return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
        } else {
            return this.instantiateWithMethodInjection(bd, beanName, owner);
        }
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, String beanName, BeanFactory owner) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner, final Constructor<?> ctor, Object... args) {
        if (bd.getMethodOverrides().isEmpty()) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        ReflectionUtils.makeAccessible(ctor);
                        return null;
                    }
                });
            }

            return BeanUtils.instantiateClass(ctor, args);
        } else {
            return this.instantiateWithMethodInjection(bd, beanName, owner, ctor, args);
        }
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, String beanName, BeanFactory owner, Constructor<?> ctor, Object... args) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner, Object factoryBean, final Method factoryMethod, Object... args) {
        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        ReflectionUtils.makeAccessible(factoryMethod);
                        return null;
                    }
                });
            } else {
                ReflectionUtils.makeAccessible(factoryMethod);
            }

            Method priorInvokedFactoryMethod = (Method)currentlyInvokedFactoryMethod.get();

            Object var18;
            try {
                currentlyInvokedFactoryMethod.set(factoryMethod);
                var18 = factoryMethod.invoke(factoryBean, args);
            } finally {
                if (priorInvokedFactoryMethod != null) {
                    currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                } else {
                    currentlyInvokedFactoryMethod.remove();
                }

            }

            return var18;
        } catch (IllegalArgumentException var15) {
            throw new BeanInstantiationException(factoryMethod, "Illegal arguments to factory method '" + factoryMethod.getName() + "'; args: " + StringUtils.arrayToCommaDelimitedString(args), var15);
        } catch (IllegalAccessException var16) {
            throw new BeanInstantiationException(factoryMethod, "Cannot access factory method '" + factoryMethod.getName() + "'; is it public?", var16);
        } catch (InvocationTargetException var17) {
            String msg = "Factory method '" + factoryMethod.getName() + "' threw exception";
            if (bd.getFactoryBeanName() != null && owner instanceof org.springframework.beans.factory.config.ConfigurableBeanFactory && ((ConfigurableBeanFactory)owner).isCurrentlyInCreation(bd.getFactoryBeanName())) {
                msg = "Circular reference involving containing bean '" + bd.getFactoryBeanName() + "' - consider declaring the factory method as static for independence from its containing instance. " + msg;
            }

            throw new BeanInstantiationException(factoryMethod, msg, var17.getTargetException());
        }
    }
}
