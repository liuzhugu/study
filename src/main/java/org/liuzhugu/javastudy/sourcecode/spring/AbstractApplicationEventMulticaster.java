package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
    private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);
    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap(64);
    private ClassLoader beanClassLoader;
    private BeanFactory beanFactory;
    private Object retrievalMutex;

    public AbstractApplicationEventMulticaster() {
        this.retrievalMutex = this.defaultRetriever;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)beanFactory;
            if (this.beanClassLoader == null) {
                this.beanClassLoader = cbf.getBeanClassLoader();
            }

            this.retrievalMutex = cbf.getSingletonMutex();
        }

    }

    private BeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans because it is not associated with a BeanFactory");
        } else {
            return this.beanFactory;
        }
    }

    public void addApplicationListener(ApplicationListener<?> listener) {
        synchronized(this.retrievalMutex) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }

            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    public void addApplicationListenerBean(String listenerBeanName) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    public void removeApplicationListener(ApplicationListener<?> listener) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    public void removeApplicationListenerBean(String listenerBeanName) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    public void removeAllListeners() {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        synchronized(this.retrievalMutex) {
            return this.defaultRetriever.getApplicationListeners();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        ListenerRetriever retriever = (ListenerRetriever)this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        } else if (this.beanClassLoader == null || ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader))) {
            synchronized(this.retrievalMutex) {
                retriever = (ListenerRetriever)this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getApplicationListeners();
                } else {
                    retriever = new ListenerRetriever(true);
                    Collection<ApplicationListener<?>> listeners = this.retrieveApplicationListeners(eventType, sourceType, retriever);
                    this.retrieverCache.put(cacheKey, retriever);
                    return listeners;
                }
            }
        } else {
            return this.retrieveApplicationListeners(eventType, sourceType, (ListenerRetriever)null);
        }
    }

    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, Class<?> sourceType, ListenerRetriever retriever) {
        LinkedList<ApplicationListener<?>> allListeners = new LinkedList();
        LinkedHashSet listeners;
        LinkedHashSet listenerBeans;
        synchronized(this.retrievalMutex) {
            listeners = new LinkedHashSet(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet(this.defaultRetriever.applicationListenerBeans);
        }

        Iterator var7 = listeners.iterator();

        while(var7.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var7.next();
            if (this.supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.applicationListeners.add(listener);
                }

                allListeners.add(listener);
            }
        }

        if (!listenerBeans.isEmpty()) {
            BeanFactory beanFactory = this.getBeanFactory();
            Iterator var15 = listenerBeans.iterator();

            while(var15.hasNext()) {
                String listenerBeanName = (String)var15.next();

                try {
                    Class<?> listenerType = beanFactory.getType(listenerBeanName);
                    if (listenerType == null || this.supportsEvent(listenerType, eventType)) {
                        ApplicationListener<?> listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener) && this.supportsEvent(listener, eventType, sourceType)) {
                            if (retriever != null) {
                                retriever.applicationListenerBeans.add(listenerBeanName);
                            }

                            allListeners.add(listener);
                        }
                    }
                } catch (NoSuchBeanDefinitionException var13) {
                }
            }
        }

        AnnotationAwareOrderComparator.sort(allListeners);
        return allListeners;
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        if (!GenericApplicationListener.class.isAssignableFrom(listenerType) && !SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
            return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
        } else {
            return true;
        }
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener)listener : new GenericApplicationListenerAdapter(listener);
        return ((GenericApplicationListener)smartListener).supportsEventType(eventType) && ((GenericApplicationListener)smartListener).supportsSourceType(sourceType);
    }

    private class ListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet();
        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            LinkedList<ApplicationListener<?>> allListeners = new LinkedList();
            Iterator var2 = this.applicationListeners.iterator();

            while(var2.hasNext()) {
                ApplicationListener<?> listener = (ApplicationListener)var2.next();
                allListeners.add(listener);
            }

            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                Iterator var8 = this.applicationListenerBeans.iterator();

                while(var8.hasNext()) {
                    String listenerBeanName = (String)var8.next();

                    try {
                        ApplicationListener<?> listenerx = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listenerx)) {
                            allListeners.add(listenerx);
                        }
                    } catch (NoSuchBeanDefinitionException var6) {
                    }
                }
            }

            AnnotationAwareOrderComparator.sort(allListeners);
            return allListeners;
        }
    }

    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {
        private final ResolvableType eventType;
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, Class<?> sourceType) {
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                ListenerCacheKey otherKey = (ListenerCacheKey)other;
                return ObjectUtils.nullSafeEquals(this.eventType, otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
            }
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.eventType) * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType.getName() + "]";
        }

        public int compareTo(ListenerCacheKey other) {
            int result = 0;
            if (this.eventType != null) {
                result = this.eventType.toString().compareTo(other.eventType.toString());
            }

            if (result == 0 && this.sourceType != null) {
                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }

            return result;
        }
    }
}
