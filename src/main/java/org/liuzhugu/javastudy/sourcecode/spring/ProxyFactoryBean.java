package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class ProxyFactoryBean extends ProxyCreatorSupport implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware {
    public static final String GLOBAL_SUFFIX = "*";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String[] interceptorNames;
    private String targetName;
    private boolean autodetectInterfaces = true;
    private boolean singleton = true;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;
    private transient ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private transient boolean classLoaderConfigured = false;
    private transient BeanFactory beanFactory;
    private boolean advisorChainInitialized = false;
    private Object singletonInstance;

    public ProxyFactoryBean() {
    }

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) throws ClassNotFoundException {
        this.setInterfaces(proxyInterfaces);
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setAutodetectInterfaces(boolean autodetectInterfaces) {
        this.autodetectInterfaces = autodetectInterfaces;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    public void setProxyClassLoader(ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }

    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.checkInterceptorNames();
    }

    public Object getObject() throws BeansException {
        this.initializeAdvisorChain();
        if (this.isSingleton()) {
            return this.getSingletonInstance();
        } else {
            if (this.targetName == null) {
                this.logger.warn("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
            }

            return this.newPrototypeInstance();
        }
    }

    public Class<?> getObjectType() {
        synchronized(this) {
            if (this.singletonInstance != null) {
                return this.singletonInstance.getClass();
            }
        }

        Class<?>[] ifcs = this.getProxiedInterfaces();
        if (ifcs.length == 1) {
            return ifcs[0];
        } else if (ifcs.length > 1) {
            return this.createCompositeInterface(ifcs);
        } else {
            return this.targetName != null && this.beanFactory != null ? this.beanFactory.getType(this.targetName) : this.getTargetClass();
        }
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.proxyClassLoader);
    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.targetSource = this.freshTargetSource();
            if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass()) {
                Class<?> targetClass = this.getTargetClass();
                if (targetClass == null) {
                    throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
                }

                this.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
            }

            super.setFrozen(this.freezeProxy);
            this.singletonInstance = this.getProxy(this.createAopProxy());
        }

        return this.singletonInstance;
    }

    private synchronized Object newPrototypeInstance() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating copy of prototype ProxyFactoryBean config: " + this);
        }

        ProxyCreatorSupport copy = new ProxyCreatorSupport(this.getAopProxyFactory());
        TargetSource targetSource = this.freshTargetSource();
        copy.copyConfigurationFrom(this, targetSource, this.freshAdvisorChain());
        if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass()) {
            copy.setInterfaces(ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(), this.proxyClassLoader));
        }

        copy.setFrozen(this.freezeProxy);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Using ProxyCreatorSupport copy: " + copy);
        }

        return this.getProxy(copy.createAopProxy());
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy(this.proxyClassLoader);
    }

    private void checkInterceptorNames() {
        if (!ObjectUtils.isEmpty(this.interceptorNames)) {
            String finalName = this.interceptorNames[this.interceptorNames.length - 1];
            if (this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE && !finalName.endsWith("*") && !this.isNamedBeanAnAdvisorOrAdvice(finalName)) {
                this.targetName = finalName;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean with name '" + finalName + "' concluding interceptor chain is not an advisor class: treating it as a target or TargetSource");
                }

                String[] newNames = new String[this.interceptorNames.length - 1];
                System.arraycopy(this.interceptorNames, 0, newNames, 0, newNames.length);
                this.interceptorNames = newNames;
            }
        }

    }

    private boolean isNamedBeanAnAdvisorOrAdvice(String beanName) {
        Class<?> namedBeanClass = this.beanFactory.getType(beanName);
        if (namedBeanClass == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not determine type of bean with name '" + beanName + "' - assuming it is neither an Advisor nor an Advice");
            }

            return false;
        } else {
            return Advisor.class.isAssignableFrom(namedBeanClass) || org.aopalliance.aop.Advice.class.isAssignableFrom(namedBeanClass);
        }
    }

    private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        if (!this.advisorChainInitialized) {
            if (!ObjectUtils.isEmpty(this.interceptorNames)) {
                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
                }

                if (this.interceptorNames[this.interceptorNames.length - 1].endsWith("*") && this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
                    throw new AopConfigException("Target required after globals");
                }

                String[] var1 = this.interceptorNames;
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    String name = var1[var3];
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Configuring advisor or advice '" + name + "'");
                    }

                    if (name.endsWith("*")) {
                        if (!(this.beanFactory instanceof ListableBeanFactory)) {
                            throw new AopConfigException("Can only use global advisors or interceptors with a ListableBeanFactory");
                        }

                        this.addGlobalAdvisor((ListableBeanFactory)this.beanFactory, name.substring(0, name.length() - "*".length()));
                    } else {
                        Object advice;
                        if (!this.singleton && !this.beanFactory.isSingleton(name)) {
                            advice = new PrototypePlaceholderAdvisor(name);
                        } else {
                            advice = this.beanFactory.getBean(name);
                        }

                        this.addAdvisorOnChainCreation(advice, name);
                    }
                }
            }

            this.advisorChainInitialized = true;
        }
    }

    private List<Advisor> freshAdvisorChain() {
        Advisor[] advisors = this.getAdvisors();
        List<Advisor> freshAdvisors = new ArrayList(advisors.length);
        Advisor[] var3 = advisors;
        int var4 = advisors.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Advisor advisor = var3[var5];
            if (advisor instanceof PrototypePlaceholderAdvisor) {
                PrototypePlaceholderAdvisor pa = (PrototypePlaceholderAdvisor)advisor;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Refreshing bean named '" + pa.getBeanName() + "'");
                }

                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve prototype advisor '" + pa.getBeanName() + "'");
                }

                Object bean = this.beanFactory.getBean(pa.getBeanName());
                Advisor refreshedAdvisor = this.namedBeanToAdvisor(bean);
                freshAdvisors.add(refreshedAdvisor);
            } else {
                freshAdvisors.add(advisor);
            }
        }

        return freshAdvisors;
    }

    private void addGlobalAdvisor(ListableBeanFactory beanFactory, String prefix) {
        String[] globalAdvisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Advisor.class);
        String[] globalInterceptorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Interceptor.class);
        List<Object> beans = new ArrayList(globalAdvisorNames.length + globalInterceptorNames.length);
        Map<Object, String> names = new HashMap(beans.size());
        String[] var7 = globalAdvisorNames;
        int var8 = globalAdvisorNames.length;

        int var9;
        String name;
        Object bean;
        for(var9 = 0; var9 < var8; ++var9) {
            name = var7[var9];
            bean = beanFactory.getBean(name);
            beans.add(bean);
            names.put(bean, name);
        }

        var7 = globalInterceptorNames;
        var8 = globalInterceptorNames.length;

        for(var9 = 0; var9 < var8; ++var9) {
            name = var7[var9];
            bean = beanFactory.getBean(name);
            beans.add(bean);
            names.put(bean, name);
        }

        AnnotationAwareOrderComparator.sort(beans);
        Iterator var12 = beans.iterator();

        while(var12.hasNext()) {
            bean = var12.next();
            name = (String)names.get(bean);
            if (name.startsWith(prefix)) {
                this.addAdvisorOnChainCreation(bean, name);
            }
        }

    }

    private void addAdvisorOnChainCreation(Object next, String name) {
        Advisor advisor = this.namedBeanToAdvisor(next);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Adding advisor with name '" + name + "'");
        }

        this.addAdvisor(advisor);
    }

    private TargetSource freshTargetSource() {
        if (this.targetName == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not refreshing target: Bean name not specified in 'interceptorNames'.");
            }

            return this.targetSource;
        } else if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve target with name '" + this.targetName + "'");
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Refreshing target with name '" + this.targetName + "'");
            }

            Object target = this.beanFactory.getBean(this.targetName);
            return (TargetSource)(target instanceof TargetSource ? (TargetSource)target : new SingletonTargetSource(target));
        }
    }

    private Advisor namedBeanToAdvisor(Object next) {
        try {
            return this.advisorAdapterRegistry.wrap(next);
        } catch (UnknownAdviceTypeException var3) {
            throw new AopConfigException("Unknown advisor type " + next.getClass() + "; Can only include Advisor or Advice type beans in interceptorNames chain except for last entry,which may also be target or TargetSource", var3);
        }
    }

    protected void adviceChanged() {
        super.adviceChanged();
        if (this.singleton) {
            this.logger.debug("Advice has changed; recaching singleton instance");
            synchronized(this) {
                this.singletonInstance = null;
            }
        }

    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
    }

    private static class PrototypePlaceholderAdvisor implements Advisor, Serializable {
        private final String beanName;
        private final String message;

        public PrototypePlaceholderAdvisor(String beanName) {
            this.beanName = beanName;
            this.message = "Placeholder for prototype Advisor/Advice with bean name '" + beanName + "'";
        }

        public String getBeanName() {
            return this.beanName;
        }

        public Advice getAdvice() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        public boolean isPerInstance() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        public String toString() {
            return this.message;
        }
    }
}