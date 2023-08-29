package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.UsesJava8;
import org.springframework.util.*;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    private static Class<?> javaUtilOptionalClass = null;
    private static Class<?> javaxInjectProviderClass = null;
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories;
    private String serializationId;
    private boolean allowBeanDefinitionOverriding = true;
    private boolean allowEagerClassLoading = true;
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver = new SimpleAutowireCandidateResolver();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap(16);
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap(64);
    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap(64);
    private volatile List<String> beanDefinitionNames = new ArrayList(256);
    private volatile Set<String> manualSingletonNames = new LinkedHashSet(16);
    private volatile String[] frozenBeanDefinitionNames;
    private volatile boolean configurationFrozen = false;

    public DefaultListableBeanFactory() {
    }

    public DefaultListableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public void setSerializationId(String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference(this));
        } else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }

        this.serializationId = serializationId;
    }

    public String getSerializationId() {
        return this.serializationId;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }

    public boolean isAllowEagerClassLoading() {
        return this.allowEagerClassLoading;
    }

    public void setDependencyComparator(Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    public void setAutowireCandidateResolver(final AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory(DefaultListableBeanFactory.this);
                        return null;
                    }
                }, this.getAccessControlContext());
            } else {
                ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory(this);
            }
        }

        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }

    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory)otherFactory;
            this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
            this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
            this.dependencyComparator = otherListableFactory.dependencyComparator;
            this.setAutowireCandidateResolver((AutowireCandidateResolver) BeanUtils.instantiateClass(this.getAutowireCandidateResolver().getClass()));
            this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
        }

    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return this.getBean(requiredType, (Object[])null);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        NamedBeanHolder<T> namedBean = this.resolveNamedBean(requiredType, args);
        if (namedBean != null) {
            return namedBean.getBeanInstance();
        } else {
            BeanFactory parent = this.getParentBeanFactory();
            if (parent != null) {
                return parent.getBean(requiredType, args);
            } else {
                throw new NoSuchBeanDefinitionException(requiredType);
            }
        }
    }

    public boolean containsBeanDefinition(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.frozenBeanDefinitionNames != null ? (String[])this.frozenBeanDefinitionNames.clone() : StringUtils.toStringArray(this.beanDefinitionNames);
    }

    public String[] getBeanNamesForType(ResolvableType type) {
        return this.doGetBeanNamesForType(type, true, true);
    }

    public String[] getBeanNamesForType(Class<?> type) {
        return this.getBeanNamesForType(type, true, true);
    }

    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        if (this.isConfigurationFrozen() && type != null && allowEagerInit) {
            Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
            String[] resolvedBeanNames = (String[])cache.get(type);
            if (resolvedBeanNames != null) {
                return resolvedBeanNames;
            } else {
                resolvedBeanNames = this.doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
                if (ClassUtils.isCacheSafe(type, this.getBeanClassLoader())) {
                    cache.put(type, resolvedBeanNames);
                }

                return resolvedBeanNames;
            }
        } else {
            return this.doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
        }
    }

    private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        List<String> result = new ArrayList();
        Iterator var5 = this.beanDefinitionNames.iterator();

        while(true) {
            String beanName;
            do {
                if (!var5.hasNext()) {
                    var5 = this.manualSingletonNames.iterator();

                    while(var5.hasNext()) {
                        beanName = (String)var5.next();

                        try {
                            if (this.isFactoryBean(beanName)) {
                                if ((includeNonSingletons || this.isSingleton(beanName)) && this.isTypeMatch(beanName, type)) {
                                    result.add(beanName);
                                    continue;
                                }

                                beanName = "&" + beanName;
                            }

                            if (this.isTypeMatch(beanName, type)) {
                                result.add(beanName);
                            }
                        } catch (NoSuchBeanDefinitionException var11) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Failed to check manually registered singleton with name '" + beanName + "'", var11);
                            }
                        }
                    }

                    return StringUtils.toStringArray(result);
                }

                beanName = (String)var5.next();
            } while(this.isAlias(beanName));

            try {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                if (!mbd.isAbstract() && (allowEagerInit || (mbd.hasBeanClass() || !mbd.isLazyInit() || this.isAllowEagerClassLoading()) && !this.requiresEagerInitForType(mbd.getFactoryBeanName()))) {
                    boolean isFactoryBean;
                    boolean var10000;
                    label161: {
                        isFactoryBean = this.isFactoryBean(beanName, mbd);
                        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
                        if (allowEagerInit || !isFactoryBean || dbd != null && !mbd.isLazyInit() || this.containsSingleton(beanName)) {
                            label157: {
                                if (!includeNonSingletons) {
                                    if (dbd != null) {
                                        if (!mbd.isSingleton()) {
                                            break label157;
                                        }
                                    } else if (!this.isSingleton(beanName)) {
                                        break label157;
                                    }
                                }

                                if (this.isTypeMatch(beanName, type)) {
                                    var10000 = true;
                                    break label161;
                                }
                            }
                        }

                        var10000 = false;
                    }

                    boolean matchFound = var10000;
                    if (!matchFound && isFactoryBean) {
                        beanName = "&" + beanName;
                        matchFound = (includeNonSingletons || mbd.isSingleton()) && this.isTypeMatch(beanName, type);
                    }

                    if (matchFound) {
                        result.add(beanName);
                    }
                }
            } catch (CannotLoadBeanClassException var12) {
                if (allowEagerInit) {
                    throw var12;
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Ignoring bean class loading failure for bean '" + beanName + "'", var12);
                }

                this.onSuppressedException(var12);
            } catch (BeanDefinitionStoreException var13) {
                if (allowEagerInit) {
                    throw var13;
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Ignoring unresolvable metadata in bean definition '" + beanName + "'", var13);
                }

                this.onSuppressedException(var13);
            }
        }
    }

    private boolean requiresEagerInitForType(String factoryBeanName) {
        return factoryBeanName != null && this.isFactoryBean(factoryBeanName) && !this.containsSingleton(factoryBeanName);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return this.getBeansOfType(type, true, true);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        String[] beanNames = this.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        Map<String, T> result = new LinkedHashMap(beanNames.length);
        String[] var6 = beanNames;
        int var7 = beanNames.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String beanName = var6[var8];

            try {
                result.put(beanName, this.getBean(beanName, type));
            } catch (BeanCreationException var13) {
                Throwable rootCause = var13.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException) {
                    BeanCreationException bce = (BeanCreationException)rootCause;
                    if (this.isCurrentlyInCreation(bce.getBeanName())) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Ignoring match to currently created bean '" + beanName + "': " + var13.getMessage());
                        }

                        this.onSuppressedException(var13);
                        continue;
                    }
                }

                throw var13;
            }
        }

        return result;
    }

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> results = new ArrayList();
        Iterator var3 = this.beanDefinitionNames.iterator();

        String beanName;
        while(var3.hasNext()) {
            beanName = (String)var3.next();
            BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
            if (!beanDefinition.isAbstract() && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }

        var3 = this.manualSingletonNames.iterator();

        while(var3.hasNext()) {
            beanName = (String)var3.next();
            if (!results.contains(beanName) && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }

        return StringUtils.toStringArray(results);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        String[] beanNames = this.getBeanNamesForAnnotation(annotationType);
        Map<String, Object> results = new LinkedHashMap(beanNames.length);
        String[] var4 = beanNames;
        int var5 = beanNames.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String beanName = var4[var6];
            results.put(beanName, this.getBean(beanName));
        }

        return results;
    }

    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        A ann = null;
        Class<?> beanType = this.getType(beanName);
        if (beanType != null) {
            ann = AnnotationUtils.findAnnotation(beanType, annotationType);
        }

        if (ann == null && this.containsBeanDefinition(beanName)) {
            BeanDefinition bd = this.getMergedBeanDefinition(beanName);
            if (bd instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
                if (abd.hasBeanClass()) {
                    ann = AnnotationUtils.findAnnotation(abd.getBeanClass(), annotationType);
                }
            }
        }

        return ann;
    }

    public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {
        Assert.notNull(dependencyType, "Dependency type must not be null");
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory) && !dependencyType.isInstance(autowiredValue)) {
                throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }

            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }

    }

    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return this.isAutowireCandidate(beanName, descriptor, this.getAutowireCandidateResolver());
    }

    protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
        String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        if (this.containsBeanDefinition(beanDefinitionName)) {
            return this.isAutowireCandidate(beanName, this.getMergedLocalBeanDefinition(beanDefinitionName), descriptor, resolver);
        } else if (this.containsSingleton(beanName)) {
            return this.isAutowireCandidate(beanName, new RootBeanDefinition(this.getType(beanName)), descriptor, resolver);
        } else {
            BeanFactory parent = this.getParentBeanFactory();
            if (parent instanceof DefaultListableBeanFactory) {
                return ((DefaultListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor, resolver);
            } else {
                return parent instanceof ConfigurableListableBeanFactory ? ((ConfigurableListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor) : true;
            }
        }
    }

    protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
        String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        this.resolveBeanClass(mbd, beanDefinitionName, new Class[0]);
        if (mbd.isFactoryMethodUnique) {
            boolean resolve;
            synchronized(mbd.constructorArgumentLock) {
                resolve = mbd.resolvedConstructorOrFactoryMethod == null;
            }

            if (resolve) {
                //(new ConstructorResolver(this)).resolveFactoryMethodIfPossible(mbd);
            }
        }

        return resolver.isAutowireCandidate(new BeanDefinitionHolder(mbd, beanName, this.getAliases(beanDefinitionName)), descriptor);
    }

    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = (BeanDefinition)this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        } else {
            return bd;
        }
    }

    public Iterator<String> getBeanNamesIterator() {
        CompositeIterator<String> iterator = new CompositeIterator();
        iterator.add(this.beanDefinitionNames.iterator());
        iterator.add(this.manualSingletonNames.iterator());
        return iterator;
    }

    public void clearMetadataCache() {
        super.clearMetadataCache();
        this.clearByTypeCache();
    }

    public void freezeConfiguration() {
        this.configurationFrozen = true;
        this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
    }

    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName);
    }

    public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Pre-instantiating singletons in " + this);
        }

        List<String> beanNames = new ArrayList(this.beanDefinitionNames);
        Iterator var2 = beanNames.iterator();

        while(true) {
            while(true) {
                String beanName;
                RootBeanDefinition bd;
                do {
                    do {
                        do {
                            if (!var2.hasNext()) {
                                var2 = beanNames.iterator();

                                while(var2.hasNext()) {
                                    beanName = (String)var2.next();
                                    Object singletonInstance = this.getSingleton(beanName);
                                    if (singletonInstance instanceof SmartInitializingSingleton) {
                                        final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton)singletonInstance;
                                        if (System.getSecurityManager() != null) {
                                            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                                                public Object run() {
                                                    smartSingleton.afterSingletonsInstantiated();
                                                    return null;
                                                }
                                            }, this.getAccessControlContext());
                                        } else {
                                            smartSingleton.afterSingletonsInstantiated();
                                        }
                                    }
                                }

                                return;
                            }

                            beanName = (String)var2.next();
                            bd = this.getMergedLocalBeanDefinition(beanName);
                        } while(bd.isAbstract());
                    } while(!bd.isSingleton());
                } while(bd.isLazyInit());

                //获取单例
                if (this.isFactoryBean(beanName)) {
                    final FactoryBean<?> factory = (FactoryBean)this.getBean("&" + beanName);
                    boolean isEagerInit;
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                            public Boolean run() {
                                return ((SmartFactoryBean)factory).isEagerInit();
                            }
                        }, this.getAccessControlContext());
                    } else {
                        isEagerInit = factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit();
                    }

                    if (isEagerInit) {
                        this.getBean(beanName);
                    }
                } else {
                    this.getBean(beanName);
                }
            }
        }
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition)beanDefinition).validate();
            } catch (BeanDefinitionValidationException var9) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", var9);
            }
        }

        BeanDefinition oldBeanDefinition = (BeanDefinition)this.beanDefinitionMap.get(beanName);
        if (oldBeanDefinition != null) {
            if (!this.isAllowBeanDefinitionOverriding()) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName + "': There is already [" + oldBeanDefinition + "] bound.");
            }

            if (oldBeanDefinition.getRole() < beanDefinition.getRole()) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (!beanDefinition.equals(oldBeanDefinition)) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
            }

            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            if (this.hasBeanCreationStarted()) {
                synchronized(this.beanDefinitionMap) {
                    this.beanDefinitionMap.put(beanName, beanDefinition);
                    List<String> updatedDefinitions = new ArrayList(this.beanDefinitionNames.size() + 1);
                    updatedDefinitions.addAll(this.beanDefinitionNames);
                    updatedDefinitions.add(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                    if (this.manualSingletonNames.contains(beanName)) {
                        Set<String> updatedSingletons = new LinkedHashSet(this.manualSingletonNames);
                        updatedSingletons.remove(beanName);
                        this.manualSingletonNames = updatedSingletons;
                    }
                }
            } else {
                this.beanDefinitionMap.put(beanName, beanDefinition);
                this.beanDefinitionNames.add(beanName);
                this.manualSingletonNames.remove(beanName);
            }

            this.frozenBeanDefinitionNames = null;
        }

        if (oldBeanDefinition != null || this.containsSingleton(beanName)) {
            this.resetBeanDefinition(beanName);
        }

    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        BeanDefinition bd = (BeanDefinition)this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }

            throw new NoSuchBeanDefinitionException(beanName);
        } else {
            if (this.hasBeanCreationStarted()) {
                synchronized(this.beanDefinitionMap) {
                    List<String> updatedDefinitions = new ArrayList(this.beanDefinitionNames);
                    updatedDefinitions.remove(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                }
            } else {
                this.beanDefinitionNames.remove(beanName);
            }

            this.frozenBeanDefinitionNames = null;
            this.resetBeanDefinition(beanName);
        }
    }

    protected void resetBeanDefinition(String beanName) {
        this.clearMergedBeanDefinition(beanName);
        this.destroySingleton(beanName);
        Iterator var2 = this.beanDefinitionNames.iterator();

        while(var2.hasNext()) {
            String bdName = (String)var2.next();
            if (!beanName.equals(bdName)) {
                BeanDefinition bd = (BeanDefinition)this.beanDefinitionMap.get(bdName);
                if (beanName.equals(bd.getParentName())) {
                    this.resetBeanDefinition(bdName);
                }
            }
        }

    }

    protected boolean allowAliasOverriding() {
        return this.isAllowBeanDefinitionOverriding();
    }

    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        if (this.hasBeanCreationStarted()) {
            synchronized(this.beanDefinitionMap) {
                if (!this.beanDefinitionMap.containsKey(beanName)) {
                    Set<String> updatedSingletons = new LinkedHashSet(this.manualSingletonNames.size() + 1);
                    updatedSingletons.addAll(this.manualSingletonNames);
                    updatedSingletons.add(beanName);
                    this.manualSingletonNames = updatedSingletons;
                }
            }
        } else if (!this.beanDefinitionMap.containsKey(beanName)) {
            this.manualSingletonNames.add(beanName);
        }

        this.clearByTypeCache();
    }

    public void destroySingleton(String beanName) {
        super.destroySingleton(beanName);
        this.manualSingletonNames.remove(beanName);
        this.clearByTypeCache();
    }

    public void destroySingletons() {
        super.destroySingletons();
        this.manualSingletonNames.clear();
        this.clearByTypeCache();
    }

    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }

    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        NamedBeanHolder<T> namedBean = this.resolveNamedBean(requiredType, (Object[])null);
        if (namedBean != null) {
            return namedBean;
        } else {
            BeanFactory parent = this.getParentBeanFactory();
            if (parent instanceof org.springframework.beans.factory.config.AutowireCapableBeanFactory) {
                return ((AutowireCapableBeanFactory)parent).resolveNamedBean(requiredType);
            } else {
                throw new NoSuchBeanDefinitionException(requiredType);
            }
        }
    }

    private <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType, Object... args) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = this.getBeanNamesForType(requiredType);
        String[] var5;
        int var6;
        int var7;
        String beanName;
        if (candidateNames.length > 1) {
            List<String> autowireCandidates = new ArrayList(candidateNames.length);
            var5 = candidateNames;
            var6 = candidateNames.length;

            for(var7 = 0; var7 < var6; ++var7) {
                beanName = var5[var7];
                if (!this.containsBeanDefinition(beanName) || this.getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }

            if (!autowireCandidates.isEmpty()) {
                candidateNames = StringUtils.toStringArray(autowireCandidates);
            }
        }

        if (candidateNames.length == 1) {
            String newBeanName = candidateNames[0];
            return new NamedBeanHolder(newBeanName, this.getBean(newBeanName, requiredType, args));
        } else if (candidateNames.length > 1) {
            Map<String, Object> candidates = new LinkedHashMap(candidateNames.length);
            var5 = candidateNames;
            var6 = candidateNames.length;

            for(var7 = 0; var7 < var6; ++var7) {
                beanName = var5[var7];
                if (this.containsSingleton(beanName)) {
                    candidates.put(beanName, this.getBean(beanName, requiredType, args));
                } else {
                    candidates.put(beanName, this.getType(beanName));
                }
            }

            String candidateName = this.determinePrimaryCandidate(candidates, requiredType);
            if (candidateName == null) {
                candidateName = this.determineHighestPriorityCandidate(candidates, requiredType);
            }

            if (candidateName != null) {
                Object beanInstance = candidates.get(candidateName);
                if (beanInstance instanceof Class) {
                    beanInstance = this.getBean(candidateName, requiredType, args);
                }

                return new NamedBeanHolder(candidateName, beanInstance);
            } else {
                throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
            }
        } else {
            return null;
        }
    }

    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        descriptor.initParameterNameDiscovery(this.getParameterNameDiscoverer());
        if (javaUtilOptionalClass == descriptor.getDependencyType()) {
            return (new DefaultListableBeanFactory.OptionalDependencyFactory()).createOptionalDependency(descriptor, requestingBeanName);
        } else if (ObjectFactory.class != descriptor.getDependencyType() && ObjectProvider.class != descriptor.getDependencyType()) {
            if (javaxInjectProviderClass == descriptor.getDependencyType()) {
                return (new DefaultListableBeanFactory.Jsr330ProviderFactory()).createDependencyProvider(descriptor, requestingBeanName);
            } else {
                //Object result = this.getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName);
                Object result = null;
                if (result == null) {
                    result = this.doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
                }

                return result;
            }
        } else {
            return new DefaultListableBeanFactory.DependencyObjectProvider(descriptor, requestingBeanName);
        }
    }

    public Object doResolveDependency(DependencyDescriptor descriptor, String beanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);

        String autowiredBeanName;
        try {
            Object shortcut = descriptor.resolveShortcut(this);
            if (shortcut != null) {
                Object var17 = shortcut;
                return var17;
            }

            Class<?> type = descriptor.getDependencyType();
            Object value = this.getAutowireCandidateResolver().getSuggestedValue(descriptor);
            Object var20;
            if (value != null) {
                if (value instanceof String) {
                    String strVal = this.resolveEmbeddedValue((String)value);
                    BeanDefinition bd = beanName != null && this.containsBean(beanName) ? this.getMergedBeanDefinition(beanName) : null;
                    value = this.evaluateBeanDefinitionString(strVal, bd);
                }

                TypeConverter converter = typeConverter != null ? typeConverter : this.getTypeConverter();
                var20 = descriptor.getField() != null ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
                return var20;
            }

            Object multipleBeans = this.resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
            if (multipleBeans != null) {
                var20 = multipleBeans;
                return var20;
            }

            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, type, descriptor);
            if (!matchingBeans.isEmpty()) {
                Object instanceCandidate;
                Object var13;
                if (matchingBeans.size() > 1) {
                    autowiredBeanName = this.determineAutowireCandidate(matchingBeans, descriptor);
                    if (autowiredBeanName == null) {
                        if (!this.isRequired(descriptor) && this.indicatesMultipleBeans(type)) {
                            var13 = null;
                            return var13;
                        }

                        var13 = descriptor.resolveNotUnique(type, matchingBeans);
                        return var13;
                    }

                    instanceCandidate = matchingBeans.get(autowiredBeanName);
                } else {
                    Map.Entry<String, Object> entry = (Map.Entry)matchingBeans.entrySet().iterator().next();
                    autowiredBeanName = (String)entry.getKey();
                    instanceCandidate = entry.getValue();
                }

                if (autowiredBeanNames != null) {
                    autowiredBeanNames.add(autowiredBeanName);
                }

                var13 = instanceCandidate instanceof Class ? descriptor.resolveCandidate(autowiredBeanName, type, this) : instanceCandidate;
                return var13;
            }

            if (this.isRequired(descriptor)) {
                this.raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
            }

            autowiredBeanName = null;
        } finally {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
        }

        return autowiredBeanName;
    }

    private Object resolveMultipleBeans(DependencyDescriptor descriptor, String beanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) {
        Class<?> type = descriptor.getDependencyType();
        Class valueType;
        Map matchingBeans;
        Class elementType;
        if (type.isArray()) {
            elementType = type.getComponentType();
            ResolvableType resolvableType = descriptor.getResolvableType();
            valueType = resolvableType.resolve();
            if (valueType != null && valueType != type) {
                type = valueType;
                elementType = resolvableType.getComponentType().resolve();
            }

            if (elementType == null) {
                return null;
            } else {
                matchingBeans = this.findAutowireCandidates(beanName, elementType, new DefaultListableBeanFactory.MultiElementDescriptor(descriptor));
                if (matchingBeans.isEmpty()) {
                    return null;
                } else {
                    if (autowiredBeanNames != null) {
                        autowiredBeanNames.addAll(matchingBeans.keySet());
                    }

                    TypeConverter converter = typeConverter != null ? typeConverter : this.getTypeConverter();
                    Object result = converter.convertIfNecessary(matchingBeans.values(), type);
                    if (this.getDependencyComparator() != null && result instanceof Object[]) {
                        Arrays.sort((Object[])((Object[])result), this.adaptDependencyComparator(matchingBeans));
                    }

                    return result;
                }
            }
        } else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            elementType = descriptor.getResolvableType().asCollection().resolveGeneric(new int[0]);
            if (elementType == null) {
                return null;
            } else {
                matchingBeans = this.findAutowireCandidates(beanName, elementType, new DefaultListableBeanFactory.MultiElementDescriptor(descriptor));
                if (matchingBeans.isEmpty()) {
                    return null;
                } else {
                    if (autowiredBeanNames != null) {
                        autowiredBeanNames.addAll(matchingBeans.keySet());
                    }

                    TypeConverter converter = typeConverter != null ? typeConverter : this.getTypeConverter();
                    Object result = converter.convertIfNecessary(matchingBeans.values(), type);
                    if (this.getDependencyComparator() != null && result instanceof List) {
                        Collections.sort((List)result, this.adaptDependencyComparator(matchingBeans));
                    }

                    return result;
                }
            }
        } else if (Map.class == type) {
            ResolvableType mapType = descriptor.getResolvableType().asMap();
            Class<?> keyType = mapType.resolveGeneric(new int[]{0});
            if (String.class != keyType) {
                return null;
            } else {
                valueType = mapType.resolveGeneric(new int[]{1});
                if (valueType == null) {
                    return null;
                } else {
                    matchingBeans = this.findAutowireCandidates(beanName, valueType, new DefaultListableBeanFactory.MultiElementDescriptor(descriptor));
                    if (matchingBeans.isEmpty()) {
                        return null;
                    } else {
                        if (autowiredBeanNames != null) {
                            autowiredBeanNames.addAll(matchingBeans.keySet());
                        }

                        return matchingBeans;
                    }
                }
            }
        } else {
            return null;
        }
    }

    private boolean isRequired(DependencyDescriptor descriptor) {
        AutowireCandidateResolver resolver = this.getAutowireCandidateResolver();
        return resolver instanceof SimpleAutowireCandidateResolver ? ((SimpleAutowireCandidateResolver)resolver).isRequired(descriptor) : descriptor.isRequired();
    }

    private boolean indicatesMultipleBeans(Class<?> type) {
        return type.isArray() || type.isInterface() && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }

    private Comparator<Object> adaptDependencyComparator(Map<String, Object> matchingBeans) {
        Comparator<Object> comparator = this.getDependencyComparator();
        return comparator instanceof OrderComparator ? ((OrderComparator)comparator).withSourceProvider(this.createFactoryAwareOrderSourceProvider(matchingBeans)) : comparator;
    }

    private DefaultListableBeanFactory.FactoryAwareOrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, Object> beans) {
        IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap();
        Iterator var3 = beans.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var3.next();
            instancesToBeanNames.put(entry.getValue(), entry.getKey());
        }

        return new DefaultListableBeanFactory.FactoryAwareOrderSourceProvider(instancesToBeanNames);
    }

    protected Map<String, Object> findAutowireCandidates(String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
        String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType, true, descriptor.isEager());
        Map<String, Object> result = new LinkedHashMap(candidateNames.length);
        Iterator var6 = this.resolvableDependencies.keySet().iterator();

        while(var6.hasNext()) {
            Class<?> autowiringType = (Class)var6.next();
            if (autowiringType.isAssignableFrom(requiredType)) {
                Object autowiringValue = this.resolvableDependencies.get(autowiringType);
                autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
                if (requiredType.isInstance(autowiringValue)) {
                    result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                    break;
                }
            }
        }

        String[] var11 = candidateNames;
        int var13 = candidateNames.length;

        int var15;
        for(var15 = 0; var15 < var13; ++var15) {
            String candidate = var11[var15];
            if (!this.isSelfReference(beanName, candidate) && this.isAutowireCandidate(candidate, descriptor)) {
                this.addCandidateEntry(result, candidate, descriptor, requiredType);
            }
        }

        if (result.isEmpty() && !this.indicatesMultipleBeans(requiredType)) {
            DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
            String[] var14 = candidateNames;
            var15 = candidateNames.length;

            String candidate;
            int var16;
            for(var16 = 0; var16 < var15; ++var16) {
                candidate = var14[var16];
                if (!this.isSelfReference(beanName, candidate) && this.isAutowireCandidate(candidate, fallbackDescriptor)) {
                    this.addCandidateEntry(result, candidate, descriptor, requiredType);
                }
            }

            if (result.isEmpty()) {
                var14 = candidateNames;
                var15 = candidateNames.length;

                for(var16 = 0; var16 < var15; ++var16) {
                    candidate = var14[var16];
                    if (this.isSelfReference(beanName, candidate) && (!(descriptor instanceof DefaultListableBeanFactory.MultiElementDescriptor) || !beanName.equals(candidate)) && this.isAutowireCandidate(candidate, fallbackDescriptor)) {
                        this.addCandidateEntry(result, candidate, descriptor, requiredType);
                    }
                }
            }
        }

        return result;
    }

    private void addCandidateEntry(Map<String, Object> candidates, String candidateName, DependencyDescriptor descriptor, Class<?> requiredType) {
        if (!(descriptor instanceof DefaultListableBeanFactory.MultiElementDescriptor) && !this.containsSingleton(candidateName)) {
            candidates.put(candidateName, this.getType(candidateName));
        } else {
            candidates.put(candidateName, descriptor.resolveCandidate(candidateName, requiredType, this));
        }

    }

    protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
        Class<?> requiredType = descriptor.getDependencyType();
        String primaryCandidate = this.determinePrimaryCandidate(candidates, requiredType);
        if (primaryCandidate != null) {
            return primaryCandidate;
        } else {
            String priorityCandidate = this.determineHighestPriorityCandidate(candidates, requiredType);
            if (priorityCandidate != null) {
                return priorityCandidate;
            } else {
                Iterator var6 = candidates.entrySet().iterator();

                String candidateName;
                Object beanInstance;
                do {
                    if (!var6.hasNext()) {
                        return null;
                    }

                    Map.Entry<String, Object> entry = (Map.Entry)var6.next();
                    candidateName = (String)entry.getKey();
                    beanInstance = entry.getValue();
                } while((beanInstance == null || !this.resolvableDependencies.containsValue(beanInstance)) && !this.matchesBeanName(candidateName, descriptor.getDependencyName()));

                return candidateName;
            }
        }
    }

    protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String primaryBeanName = null;
        Iterator var4 = candidates.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var4.next();
            String candidateBeanName = (String)entry.getKey();
            Object beanInstance = entry.getValue();
            if (this.isPrimary(candidateBeanName, beanInstance)) {
                if (primaryBeanName != null) {
                    boolean candidateLocal = this.containsBeanDefinition(candidateBeanName);
                    boolean primaryLocal = this.containsBeanDefinition(primaryBeanName);
                    if (candidateLocal && primaryLocal) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "more than one 'primary' bean found among candidates: " + candidates.keySet());
                    }

                    if (candidateLocal) {
                        primaryBeanName = candidateBeanName;
                    }
                } else {
                    primaryBeanName = candidateBeanName;
                }
            }
        }

        return primaryBeanName;
    }

    protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String highestPriorityBeanName = null;
        Integer highestPriority = null;
        Iterator var5 = candidates.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var5.next();
            String candidateBeanName = (String)entry.getKey();
            Object beanInstance = entry.getValue();
            Integer candidatePriority = this.getPriority(beanInstance);
            if (candidatePriority != null) {
                if (highestPriorityBeanName != null) {
                    if (candidatePriority.equals(highestPriority)) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());
                    }

                    if (candidatePriority < highestPriority) {
                        highestPriorityBeanName = candidateBeanName;
                        highestPriority = candidatePriority;
                    }
                } else {
                    highestPriorityBeanName = candidateBeanName;
                    highestPriority = candidatePriority;
                }
            }
        }

        return highestPriorityBeanName;
    }

    protected boolean isPrimary(String beanName, Object beanInstance) {
        if (this.containsBeanDefinition(beanName)) {
            return this.getMergedLocalBeanDefinition(beanName).isPrimary();
        } else {
            BeanFactory parent = this.getParentBeanFactory();
            return parent instanceof DefaultListableBeanFactory && ((DefaultListableBeanFactory)parent).isPrimary(beanName, beanInstance);
        }
    }

    protected Integer getPriority(Object beanInstance) {
        Comparator<Object> comparator = this.getDependencyComparator();
        return comparator instanceof OrderComparator ? ((OrderComparator)comparator).getPriority(beanInstance) : null;
    }

    protected boolean matchesBeanName(String beanName, String candidateName) {
        return candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(this.getAliases(beanName), candidateName));
    }

    private boolean isSelfReference(String beanName, String candidateName) {
        return beanName != null && candidateName != null && (beanName.equals(candidateName) || this.containsBeanDefinition(candidateName) && beanName.equals(this.getMergedLocalBeanDefinition(candidateName).getFactoryBeanName()));
    }

    private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
        this.checkBeanNotOfRequiredType(type, descriptor);
        throw new NoSuchBeanDefinitionException(resolvableType, "expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: " + ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
    }

    private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
        Iterator var3 = this.beanDefinitionNames.iterator();

        while(var3.hasNext()) {
            String beanName = (String)var3.next();
            RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            Class<?> targetType = mbd.getTargetType();
            if (targetType != null && type.isAssignableFrom(targetType) && this.isAutowireCandidate(beanName, mbd, descriptor, this.getAutowireCandidateResolver())) {
                Object beanInstance = this.getSingleton(beanName, false);
                Class<?> beanType = beanInstance != null ? beanInstance.getClass() : this.predictBeanType(beanName, mbd, new Class[0]);
                if (!type.isAssignableFrom(beanType)) {
                    throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
                }
            }
        }

        BeanFactory parent = this.getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory)parent).checkBeanNotOfRequiredType(type, descriptor);
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.collectionToCommaDelimitedString(this.beanDefinitionNames));
        sb.append("]; ");
        BeanFactory parent = this.getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        } else {
            sb.append("parent: ").append(ObjectUtils.identityToString(parent));
        }

        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.serializationId != null) {
            return new DefaultListableBeanFactory.SerializedBeanFactoryReference(this.serializationId);
        } else {
            throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
        }
    }

    static {
        try {
            javaUtilOptionalClass = ClassUtils.forName("java.util.Optional", DefaultListableBeanFactory.class.getClassLoader());
        } catch (ClassNotFoundException var2) {
        }

        try {
            javaxInjectProviderClass = ClassUtils.forName("org.liuzhugu.javastudy.sourcecode.spring.Provider", DefaultListableBeanFactory.class.getClassLoader());
        } catch (ClassNotFoundException var1) {
        }

        serializableFactories = new ConcurrentHashMap(8);
    }

    private static class MultiElementDescriptor extends DefaultListableBeanFactory.NestedDependencyDescriptor {
        public MultiElementDescriptor(DependencyDescriptor original) {
            super(original);
        }
    }

    private static class NestedDependencyDescriptor extends DependencyDescriptor {
        public NestedDependencyDescriptor(DependencyDescriptor original) {
            super(original);
            this.increaseNestingLevel();
        }
    }

    private class FactoryAwareOrderSourceProvider implements OrderComparator.OrderSourceProvider {
        private final Map<Object, String> instancesToBeanNames;

        public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
            this.instancesToBeanNames = instancesToBeanNames;
        }

        public Object getOrderSource(Object obj) {
            RootBeanDefinition beanDefinition = this.getRootBeanDefinition((String)this.instancesToBeanNames.get(obj));
            if (beanDefinition == null) {
                return null;
            } else {
                List<Object> sources = new ArrayList(2);
                Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
                if (factoryMethod != null) {
                    sources.add(factoryMethod);
                }

                Class<?> targetType = beanDefinition.getTargetType();
                if (targetType != null && targetType != obj.getClass()) {
                    sources.add(targetType);
                }

                return sources.toArray();
            }
        }

        private RootBeanDefinition getRootBeanDefinition(String beanName) {
            if (beanName != null && DefaultListableBeanFactory.this.containsBeanDefinition(beanName)) {
                BeanDefinition bd = DefaultListableBeanFactory.this.getMergedBeanDefinition(beanName);
                if (bd instanceof RootBeanDefinition) {
                    return (RootBeanDefinition)bd;
                }
            }

            return null;
        }
    }

    private class Jsr330ProviderFactory {
        private Jsr330ProviderFactory() {
        }

        public Object createDependencyProvider(DependencyDescriptor descriptor, String beanName) {
            return DefaultListableBeanFactory.this.new Jsr330DependencyProvider(descriptor, beanName);
        }
    }

    private class Jsr330DependencyProvider extends DefaultListableBeanFactory.DependencyObjectProvider implements Provider<Object> {
        public Jsr330DependencyProvider(DependencyDescriptor descriptor, String beanName) {
            super(descriptor, beanName);
        }

        public Object get() throws BeansException {
            return this.getObject();
        }
    }

    private class DependencyObjectProvider implements ObjectProvider<Object>, Serializable {
        private final DependencyDescriptor descriptor;
        private final boolean optional;
        private final String beanName;

        public DependencyObjectProvider(DependencyDescriptor descriptor, String beanName) {
            this.descriptor = new DefaultListableBeanFactory.NestedDependencyDescriptor(descriptor);
            this.optional = this.descriptor.getDependencyType() == DefaultListableBeanFactory.javaUtilOptionalClass;
            this.beanName = beanName;
        }

        public Object getObject() throws BeansException {
            return this.optional ? (DefaultListableBeanFactory.this.new OptionalDependencyFactory()).createOptionalDependency(this.descriptor, this.beanName) : DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, (Set)null, (TypeConverter)null);
        }

        public Object getObject(final Object... args) throws BeansException {
            if (this.optional) {
                return (DefaultListableBeanFactory.this.new OptionalDependencyFactory()).createOptionalDependency(this.descriptor, this.beanName, args);
            } else {
                DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) {
                    public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                        return ((AbstractBeanFactory)beanFactory).getBean(beanName, requiredType, args);
                    }
                };
                return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set)null, (TypeConverter)null);
            }
        }

        public Object getIfAvailable() throws BeansException {
            if (this.optional) {
                return (DefaultListableBeanFactory.this.new OptionalDependencyFactory()).createOptionalDependency(this.descriptor, this.beanName);
            } else {
                DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) {
                    public boolean isRequired() {
                        return false;
                    }
                };
                return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set)null, (TypeConverter)null);
            }
        }

        public Object getIfUnique() throws BeansException {
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) {
                public boolean isRequired() {
                    return false;
                }

                public Object resolveNotUnique(Class<?> type, Map<String, Object> matchingBeans) {
                    return null;
                }
            };
            return this.optional ? (DefaultListableBeanFactory.this.new OptionalDependencyFactory()).createOptionalDependency(descriptorToUse, this.beanName) : DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set)null, (TypeConverter)null);
        }
    }

    @UsesJava8
    private class OptionalDependencyFactory {
        private OptionalDependencyFactory() {
        }

        public Object createOptionalDependency(DependencyDescriptor descriptor, String beanName, final Object... args) {
            DependencyDescriptor descriptorToUse = new DefaultListableBeanFactory.NestedDependencyDescriptor(descriptor) {
                public boolean isRequired() {
                    return false;
                }

                public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                    return !ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName, new Object[]{requiredType, args}) : super.resolveCandidate(beanName, requiredType, beanFactory);
                }
            };
            return Optional.ofNullable(DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, beanName, (Set)null, (TypeConverter)null));
        }
    }

    private static class SerializedBeanFactoryReference implements Serializable {
        private final String id;

        public SerializedBeanFactoryReference(String id) {
            this.id = id;
        }

        private Object readResolve() {
            Reference<?> ref = (Reference) DefaultListableBeanFactory.serializableFactories.get(this.id);
            if (ref != null) {
                Object result = ref.get();
                if (result != null) {
                    return result;
                }
            }

            return new DefaultListableBeanFactory();
        }
    }
}

