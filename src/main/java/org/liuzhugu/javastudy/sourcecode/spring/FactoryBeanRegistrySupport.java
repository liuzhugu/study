package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;

import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap(16);

    public FactoryBeanRegistrySupport() {
    }

    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        try {
            return System.getSecurityManager() != null ? (Class) AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
                public Class<?> run() {
                    return factoryBean.getObjectType();
                }
            }, this.getAccessControlContext()) : factoryBean.getObjectType();
        } catch (Throwable var3) {
            this.logger.warn("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", var3);
            return null;
        }
    }

    protected Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return object != NULL_OBJECT ? object : null;
    }

    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && this.containsSingleton(beanName)) {
            synchronized(this.getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = this.doGetObjectFromFactoryBean(factory, beanName);
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    } else {
                        if (object != null && shouldPostProcess) {
                            if (this.isSingletonCurrentlyInCreation(beanName)) {
                                return object;
                            }

                            this.beforeSingletonCreation(beanName);

                            try {
                                object = this.postProcessObjectFromFactoryBean(object, beanName);
                            } catch (Throwable var14) {
                                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", var14);
                            } finally {
                                this.afterSingletonCreation(beanName);
                            }
                        }

                        if (this.containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object != null ? object : NULL_OBJECT);
                        }
                    }
                }

                return object != NULL_OBJECT ? object : null;
            }
        } else {
            Object object = this.doGetObjectFromFactoryBean(factory, beanName);
            if (object != null && shouldPostProcess) {
                try {
                    object = this.postProcessObjectFromFactoryBean(object, beanName);
                } catch (Throwable var17) {
                    throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", var17);
                }
            }

            return object;
        }
    }

    private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, String beanName) throws BeanCreationException {
        Object object;
        try {
            if (System.getSecurityManager() != null) {
                AccessControlContext acc = this.getAccessControlContext();

                try {
                    object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            return factory.getObject();
                        }
                    }, acc);
                } catch (PrivilegedActionException var6) {
                    throw var6.getException();
                }
            } else {
                object = factory.getObject();
            }
        } catch (FactoryBeanNotInitializedException var7) {
            throw new BeanCurrentlyInCreationException(beanName, var7.toString());
        } catch (Throwable var8) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", var8);
        }

        if (object == null && this.isSingletonCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
        } else {
            return object;
        }
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        } else {
            return (FactoryBean)beanInstance;
        }
    }

    protected void removeSingleton(String beanName) {
        synchronized(this.getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanObjectCache.remove(beanName);
        }
    }

    protected void clearSingletonCache() {
        synchronized(this.getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanObjectCache.clear();
        }
    }

    protected AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }
}
