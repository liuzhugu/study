package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.util.*;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable {
    private static final String CLOSE_METHOD_NAME = "close";
    private static final String SHUTDOWN_METHOD_NAME = "shutdown";
    private static final Log logger = LogFactory.getLog(DisposableBeanAdapter.class);
    private static Class<?> closeableInterface;
    private final Object bean;
    private final String beanName;
    private final boolean invokeDisposableBean;
    private final boolean nonPublicAccessAllowed;
    private final AccessControlContext acc;
    private String destroyMethodName;
    private transient Method destroyMethod;
    private List<DestructionAwareBeanPostProcessor> beanPostProcessors;

    public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition, List<BeanPostProcessor> postProcessors, AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = this.bean instanceof DisposableBean && !beanDefinition.isExternallyManagedDestroyMethod("destroy");
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.acc = acc;
        String destroyMethodName = this.inferDestroyMethodIfNecessary(bean, beanDefinition);
        if (destroyMethodName != null && (!this.invokeDisposableBean || !"destroy".equals(destroyMethodName)) && !beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName)) {
            this.destroyMethodName = destroyMethodName;
            this.destroyMethod = this.determineDestroyMethod();
            if (this.destroyMethod == null) {
                if (beanDefinition.isEnforceDestroyMethod()) {
                    throw new BeanDefinitionValidationException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
                }
            } else {
                Class<?>[] paramTypes = this.destroyMethod.getParameterTypes();
                if (paramTypes.length > 1) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
                }

                if (paramTypes.length == 1 && Boolean.TYPE != paramTypes[0]) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
                }
            }
        }

        this.beanPostProcessors = this.filterPostProcessors(postProcessors, bean);
    }

    public DisposableBeanAdapter(Object bean, List<BeanPostProcessor> postProcessors, AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = null;
        this.invokeDisposableBean = this.bean instanceof DisposableBean;
        this.nonPublicAccessAllowed = true;
        this.acc = acc;
        this.beanPostProcessors = this.filterPostProcessors(postProcessors, bean);
    }

    private DisposableBeanAdapter(Object bean, String beanName, boolean invokeDisposableBean, boolean nonPublicAccessAllowed, String destroyMethodName, List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = invokeDisposableBean;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.acc = null;
        this.destroyMethodName = destroyMethodName;
        this.beanPostProcessors = postProcessors;
    }

    private String inferDestroyMethodIfNecessary(Object bean, RootBeanDefinition beanDefinition) {
        String destroyMethodName = beanDefinition.getDestroyMethodName();
        if ("(inferred)".equals(destroyMethodName) || destroyMethodName == null && closeableInterface.isInstance(bean)) {
            if (!(bean instanceof DisposableBean)) {
                try {
                    return bean.getClass().getMethod("close").getName();
                } catch (NoSuchMethodException var7) {
                    try {
                        return bean.getClass().getMethod("shutdown").getName();
                    } catch (NoSuchMethodException var6) {
                    }
                }
            }

            return null;
        } else {
            return StringUtils.hasLength(destroyMethodName) ? destroyMethodName : null;
        }
    }

    private List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<BeanPostProcessor> processors, Object bean) {
        List<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (!CollectionUtils.isEmpty(processors)) {
            filteredPostProcessors = new ArrayList(processors.size());
            Iterator var4 = processors.iterator();

            while(var4.hasNext()) {
                BeanPostProcessor processor = (BeanPostProcessor)var4.next();
                if (processor instanceof DestructionAwareBeanPostProcessor) {
                    DestructionAwareBeanPostProcessor dabpp = (DestructionAwareBeanPostProcessor)processor;

                    try {
                        if (dabpp.requiresDestruction(bean)) {
                            filteredPostProcessors.add(dabpp);
                        }
                    } catch (AbstractMethodError var8) {
                        filteredPostProcessors.add(dabpp);
                    }
                }
            }
        }

        return filteredPostProcessors;
    }

    public void run() {
        this.destroy();
    }

    public void destroy() {
        if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
            Iterator var1 = this.beanPostProcessors.iterator();

            while(var1.hasNext()) {
                DestructionAwareBeanPostProcessor processor = (DestructionAwareBeanPostProcessor)var1.next();
                processor.postProcessBeforeDestruction(this.bean, this.beanName);
            }
        }

        if (this.invokeDisposableBean) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking destroy() on bean with name '" + this.beanName + "'");
            }

            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            ((DisposableBean) DisposableBeanAdapter.this.bean).destroy();
                            return null;
                        }
                    }, this.acc);
                } else {
                    ((DisposableBean)this.bean).destroy();
                }
            } catch (Throwable var3) {
                String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                if (logger.isDebugEnabled()) {
                    logger.warn(msg, var3);
                } else {
                    logger.warn(msg + ": " + var3);
                }
            }
        }

        if (this.destroyMethod != null) {
            this.invokeCustomDestroyMethod(this.destroyMethod);
        } else if (this.destroyMethodName != null) {
            Method methodToCall = this.determineDestroyMethod();
            if (methodToCall != null) {
                this.invokeCustomDestroyMethod(methodToCall);
            }
        }

    }

    private Method determineDestroyMethod() {
        try {
            return System.getSecurityManager() != null ? (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
                public Method run() {
                    return DisposableBeanAdapter.this.findDestroyMethod();
                }
            }) : this.findDestroyMethod();
        } catch (IllegalArgumentException var2) {
            throw new BeanDefinitionValidationException("Could not find unique destroy method on bean with name '" + this.beanName + ": " + var2.getMessage());
        }
    }

    private Method findDestroyMethod() {
        return this.nonPublicAccessAllowed ? BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), this.destroyMethodName) : BeanUtils.findMethodWithMinimalParameters(this.bean.getClass().getMethods(), this.destroyMethodName);
    }

    private void invokeCustomDestroyMethod(final Method destroyMethod) {
        Class<?>[] paramTypes = destroyMethod.getParameterTypes();
        final Object[] args = new Object[paramTypes.length];
        if (paramTypes.length == 1) {
            args[0] = Boolean.TRUE;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Invoking destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'");
        }

        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        ReflectionUtils.makeAccessible(destroyMethod);
                        return null;
                    }
                });

                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            destroyMethod.invoke(DisposableBeanAdapter.this.bean, args);
                            return null;
                        }
                    }, this.acc);
                } catch (PrivilegedActionException var6) {
                    throw (InvocationTargetException)var6.getException();
                }
            } else {
                ReflectionUtils.makeAccessible(destroyMethod);
                destroyMethod.invoke(this.bean, args);
            }
        } catch (InvocationTargetException var7) {
            String msg = "Invocation of destroy method '" + this.destroyMethodName + "' failed on bean with name '" + this.beanName + "'";
            if (logger.isDebugEnabled()) {
                logger.warn(msg, var7.getTargetException());
            } else {
                logger.warn(msg + ": " + var7.getTargetException());
            }
        } catch (Throwable var8) {
            logger.error("Couldn't invoke destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'", var8);
        }

    }

    protected Object writeReplace() {
        List<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList();
            Iterator var2 = this.beanPostProcessors.iterator();

            while(var2.hasNext()) {
                DestructionAwareBeanPostProcessor postProcessor = (DestructionAwareBeanPostProcessor)var2.next();
                if (postProcessor instanceof Serializable) {
                    serializablePostProcessors.add(postProcessor);
                }
            }
        }

        return new DisposableBeanAdapter(this.bean, this.beanName, this.invokeDisposableBean, this.nonPublicAccessAllowed, this.destroyMethodName, serializablePostProcessors);
    }

    public static boolean hasDestroyMethod(Object bean, RootBeanDefinition beanDefinition) {
        if (!(bean instanceof DisposableBean) && !closeableInterface.isInstance(bean)) {
            String destroyMethodName = beanDefinition.getDestroyMethodName();
            if (!"(inferred)".equals(destroyMethodName)) {
                return StringUtils.hasLength(destroyMethodName);
            } else {
                return ClassUtils.hasMethod(bean.getClass(), "close", new Class[0]) || ClassUtils.hasMethod(bean.getClass(), "shutdown", new Class[0]);
            }
        } else {
            return true;
        }
    }

    public static boolean hasApplicableProcessors(Object bean, List<BeanPostProcessor> postProcessors) {
        if (!CollectionUtils.isEmpty(postProcessors)) {
            Iterator var2 = postProcessors.iterator();

            while(var2.hasNext()) {
                BeanPostProcessor processor = (BeanPostProcessor)var2.next();
                if (processor instanceof DestructionAwareBeanPostProcessor) {
                    DestructionAwareBeanPostProcessor dabpp = (DestructionAwareBeanPostProcessor)processor;

                    try {
                        if (dabpp.requiresDestruction(bean)) {
                            return true;
                        }
                    } catch (AbstractMethodError var6) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    static {
        try {
            closeableInterface = ClassUtils.forName("java.lang.AutoCloseable", DisposableBeanAdapter.class.getClassLoader());
        } catch (ClassNotFoundException var1) {
            closeableInterface = Closeable.class;
        }

    }
}
