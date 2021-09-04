package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    private InstantiationStrategy instantiationStrategy;
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private boolean allowCircularReferences;
    private boolean allowRawInjectionDespiteWrapping;
    private final Set<Class<?>> ignoredDependencyTypes;
    private final Set<Class<?>> ignoredDependencyInterfaces;
    private final Map<String, BeanWrapper> factoryBeanInstanceCache;
    private final ConcurrentMap<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache;

    public AbstractAutowireCapableBeanFactory() {
        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.allowCircularReferences = true;
        this.allowRawInjectionDespiteWrapping = false;
        this.ignoredDependencyTypes = new HashSet();
        this.ignoredDependencyInterfaces = new HashSet();
        this.factoryBeanInstanceCache = new ConcurrentHashMap(16);
        this.filteredPropertyDescriptorsCache = new ConcurrentHashMap(256);
        this.ignoreDependencyInterface(BeanNameAware.class);
        this.ignoreDependencyInterface(BeanFactoryAware.class);
        this.ignoreDependencyInterface(BeanClassLoaderAware.class);
    }

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        this();
        this.setParentBeanFactory(parentBeanFactory);
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    protected InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
        this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
    }

    public void ignoreDependencyType(Class<?> type) {
        this.ignoredDependencyTypes.add(type);
    }

    public void ignoreDependencyInterface(Class<?> ifc) {
        this.ignoredDependencyInterfaces.add(ifc);
    }

    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory)otherFactory;
            this.instantiationStrategy = otherAutowireFactory.instantiationStrategy;
            this.allowCircularReferences = otherAutowireFactory.allowCircularReferences;
            this.ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
            this.ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
        }

    }

    public <T> T createBean(Class<T> beanClass) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass);
        bd.setScope("prototype");
        bd.allowCaching = ClassUtils.isCacheSafe(beanClass, this.getBeanClassLoader());
        return (T)this.createBean(beanClass.getName(), bd, (Object[])null);
    }

    public void autowireBean(Object existingBean) {
        RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean));
        bd.setScope("prototype");
        bd.allowCaching = ClassUtils.isCacheSafe(bd.getBeanClass(), this.getBeanClassLoader());
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(bd.getBeanClass().getName(), bd, bw);
    }

    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        BeanDefinition mbd = this.getMergedBeanDefinition(beanName);
        RootBeanDefinition bd = null;
        if (mbd instanceof RootBeanDefinition) {
            RootBeanDefinition rbd = (RootBeanDefinition)mbd;
            bd = rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition();
        }

        if (!mbd.isPrototype()) {
            if (bd == null) {
                bd = new RootBeanDefinition(mbd);
            }

            bd.setScope("prototype");
            bd.allowCaching = ClassUtils.isCacheSafe(ClassUtils.getUserClass(existingBean), this.getBeanClassLoader());
        }

        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(beanName, bd, bw);
        return this.initializeBean(beanName, existingBean, bd);
    }

    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        return this.resolveDependency(descriptor, requestingBeanName, (Set)null, (TypeConverter)null);
    }

    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        return this.createBean(beanClass.getName(), bd, (Object[])null);
    }

    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        final RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        if (bd.getResolvedAutowireMode() == 3) {
            return this.autowireConstructor(beanClass.getName(), bd, (Constructor[])null, (Object[])null).getWrappedInstance();
        } else {
            Object bean;
            if (System.getSecurityManager() != null) {
                bean = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        return AbstractAutowireCapableBeanFactory.this.getInstantiationStrategy().instantiate(bd, (String)null, AbstractAutowireCapableBeanFactory.this);
                    }
                }, this.getAccessControlContext());
            } else {
                bean = this.getInstantiationStrategy().instantiate(bd, (String)null, this);
            }

            this.populateBean(beanClass.getName(), bd, new BeanWrapperImpl(bean));
            return bean;
        }
    }

    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        if (autowireMode == 3) {
            throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
        } else {
            RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean), autowireMode, dependencyCheck);
            bd.setScope("prototype");
            BeanWrapper bw = new BeanWrapperImpl(existingBean);
            this.initBeanWrapper(bw);
            this.populateBean(bd.getBeanClass().getName(), bd, bw);
        }
    }

    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        BeanDefinition bd = this.getMergedBeanDefinition(beanName);
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.applyPropertyValues(beanName, bd, bw, bd.getPropertyValues());
    }

    public Object initializeBean(Object existingBean, String beanName) {
        return this.initializeBean(beanName, existingBean, (RootBeanDefinition)null);
    }

    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        Iterator var4 = this.getBeanPostProcessors().iterator();

        do {
            if (!var4.hasNext()) {
                return result;
            }

            BeanPostProcessor beanProcessor = (BeanPostProcessor)var4.next();
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
        } while(result != null);

        return result;
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        Iterator var4 = this.getBeanPostProcessors().iterator();

        do {
            if (!var4.hasNext()) {
                return result;
            }

            BeanPostProcessor beanProcessor = (BeanPostProcessor)var4.next();
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
        } while(result != null);

        return result;
    }

    public void destroyBean(Object existingBean) {
        (new DisposableBeanAdapter(existingBean, this.getBeanPostProcessors(), this.getAccessControlContext())).destroy();
    }

    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating instance of bean '" + beanName + "'");
        }

        RootBeanDefinition mbdToUse = mbd;
        Class<?> resolvedClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
        }

        try {
            mbdToUse.prepareMethodOverrides();
        } catch (BeanDefinitionValidationException var7) {
            throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", var7);
        }

        Object beanInstance;
        try {
            beanInstance = this.resolveBeforeInstantiation(beanName, mbdToUse);
            if (beanInstance != null) {
                return beanInstance;
            }
        } catch (Throwable var8) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", var8);
        }

        beanInstance = this.doCreateBean(beanName, mbdToUse, args);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Finished creating instance of bean '" + beanName + "'");
        }

        return beanInstance;
    }

    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = (BeanWrapper)this.factoryBeanInstanceCache.remove(beanName);
        }

        if (instanceWrapper == null) {
            instanceWrapper = this.createBeanInstance(beanName, mbd, args);
        }

        final Object bean = instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null;
        Class<?> beanType = instanceWrapper != null ? instanceWrapper.getWrappedClass() : null;
        mbd.resolvedTargetType = beanType;
        synchronized(mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                try {
                    //对注入元素的预解析
                    this.applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                } catch (Throwable var17) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", var17);
                }

                mbd.postProcessed = true;
            }
        }

        boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
            }

            this.addSingletonFactory(beanName, new ObjectFactory<Object>() {
                public Object getObject() throws BeansException {
                    return AbstractAutowireCapableBeanFactory.this.getEarlyBeanReference(beanName, mbd, bean);
                }
            });
        }

        Object exposedObject = bean;

        try {
            this.populateBean(beanName, mbd, instanceWrapper);
            if (exposedObject != null) {
                exposedObject = this.initializeBean(beanName, exposedObject, mbd);
            }
        } catch (Throwable var18) {
            if (var18 instanceof BeanCreationException && beanName.equals(((BeanCreationException)var18).getBeanName())) {
                throw (BeanCreationException)var18;
            }

            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", var18);
        }

        if (earlySingletonExposure) {
            Object earlySingletonReference = this.getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                } else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                    String[] dependentBeans = this.getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet(dependentBeans.length);
                    String[] var12 = dependentBeans;
                    int var13 = dependentBeans.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        String dependentBean = var12[var14];
                        if (!this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }

                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }

        try {
            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
            return exposedObject;
        } catch (BeanDefinitionValidationException var16) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", var16);
        }
    }

    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = this.determineTargetType(beanName, mbd, typesToMatch);
        if (targetType != null && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            Iterator var5 = this.getBeanPostProcessors().iterator();

            while(var5.hasNext()) {
                BeanPostProcessor bp = (BeanPostProcessor)var5.next();
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    Class<?> predicted = ibp.predictBeanType(targetType, beanName);
                    if (predicted != null && (typesToMatch.length != 1 || FactoryBean.class != typesToMatch[0] || FactoryBean.class.isAssignableFrom(predicted))) {
                        return predicted;
                    }
                }
            }
        }

        return targetType;
    }

    protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType == null) {
            targetType = mbd.getFactoryMethodName() != null ? this.getTypeForFactoryMethod(beanName, mbd, typesToMatch) : this.resolveBeanClass(mbd, beanName, typesToMatch);
            if (ObjectUtils.isEmpty(typesToMatch) || this.getTempClassLoader() == null) {
                mbd.resolvedTargetType = targetType;
            }
        }

        return targetType;
    }

    protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
        if (cachedReturnType != null) {
            return cachedReturnType.resolve();
        } else {
            boolean isStatic = true;
            String factoryBeanName = mbd.getFactoryBeanName();
            Class factoryClass;
            if (factoryBeanName != null) {
                if (factoryBeanName.equals(beanName)) {
                    throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
                }

                factoryClass = this.getType(factoryBeanName);
                isStatic = false;
            } else {
                factoryClass = this.resolveBeanClass(mbd, beanName, typesToMatch);
            }

            if (factoryClass == null) {
                return null;
            } else {
                factoryClass = ClassUtils.getUserClass(factoryClass);
                Class<?> commonType = null;
                Method uniqueCandidate = null;
                int minNrOfArgs = mbd.getConstructorArgumentValues().getArgumentCount();
                Method[] candidates = ReflectionUtils.getUniqueDeclaredMethods(factoryClass);
                Method[] var12 = candidates;
                int var13 = candidates.length;

                for(int var14 = 0; var14 < var13; ++var14) {
                    Method factoryMethod = var12[var14];
                    if (Modifier.isStatic(factoryMethod.getModifiers()) == isStatic && factoryMethod.getName().equals(mbd.getFactoryMethodName()) && factoryMethod.getParameterTypes().length >= minNrOfArgs) {
                        if (factoryMethod.getTypeParameters().length > 0) {
                            try {
                                Class<?>[] paramTypes = factoryMethod.getParameterTypes();
                                String[] paramNames = null;
                                ParameterNameDiscoverer pnd = this.getParameterNameDiscoverer();
                                if (pnd != null) {
                                    paramNames = pnd.getParameterNames(factoryMethod);
                                }

                                ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
                                Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet(paramTypes.length);
                                Object[] args = new Object[paramTypes.length];

                                for(int i = 0; i < args.length; ++i) {
                                    ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], paramNames != null ? paramNames[i] : null, usedValueHolders);
                                    if (valueHolder == null) {
                                        valueHolder = cav.getGenericArgumentValue((Class)null, (String)null, usedValueHolders);
                                    }

                                    if (valueHolder != null) {
                                        args[i] = valueHolder.getValue();
                                        usedValueHolders.add(valueHolder);
                                    }
                                }

                                Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(factoryMethod, args, this.getBeanClassLoader());
                                if (returnType != null) {
                                    uniqueCandidate = commonType == null && returnType == factoryMethod.getReturnType() ? factoryMethod : null;
                                    commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
                                    if (commonType == null) {
                                        return null;
                                    }
                                }
                            } catch (Throwable var24) {
                                if (this.logger.isDebugEnabled()) {
                                    this.logger.debug("Failed to resolve generic return type for factory method: " + var24);
                                }
                            }
                        } else {
                            uniqueCandidate = commonType == null ? factoryMethod : null;
                            commonType = ClassUtils.determineCommonAncestor(factoryMethod.getReturnType(), commonType);
                            if (commonType == null) {
                                return null;
                            }
                        }
                    }
                }

                if (commonType == null) {
                    return null;
                } else {
                    cachedReturnType = uniqueCandidate != null ? ResolvableType.forMethodReturnType(uniqueCandidate) : ResolvableType.forClass(commonType);
                    mbd.factoryMethodReturnType = cachedReturnType;
                    return cachedReturnType.resolve();
                }
            }
        }
    }

    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        String factoryBeanName = mbd.getFactoryBeanName();
        String factoryMethodName = mbd.getFactoryMethodName();
        if (factoryBeanName != null) {
            if (factoryMethodName != null) {
                BeanDefinition fbDef = this.getBeanDefinition(factoryBeanName);
                if (fbDef instanceof AbstractBeanDefinition) {
                    AbstractBeanDefinition afbDef = (AbstractBeanDefinition)fbDef;
                    if (afbDef.hasBeanClass()) {
                        Class<?> result = this.getTypeForFactoryBeanFromMethod(afbDef.getBeanClass(), factoryMethodName);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }

            if (!this.isBeanEligibleForMetadataCaching(factoryBeanName)) {
                return null;
            }
        }

        FactoryBean<?> fb = mbd.isSingleton() ? this.getSingletonFactoryBeanForTypeCheck(beanName, mbd) : this.getNonSingletonFactoryBeanForTypeCheck(beanName, mbd);
        if (fb != null) {
            Class<?> result = this.getTypeForFactoryBean(fb);
            return result != null ? result : super.getTypeForFactoryBean(beanName, mbd);
        } else if (factoryBeanName == null && mbd.hasBeanClass()) {
            return factoryMethodName != null ? this.getTypeForFactoryBeanFromMethod(mbd.getBeanClass(), factoryMethodName) : GenericTypeResolver.resolveTypeArgument(mbd.getBeanClass(), FactoryBean.class);
        } else {
            return null;
        }
    }

    private Class<?> getTypeForFactoryBeanFromMethod(Class<?> beanClass, final String factoryMethodName) {
        class Holder {
            Class<?> value = null;

            Holder() {
            }
        }

        final Holder objectType = new Holder();
        Class<?> fbClass = ClassUtils.getUserClass(beanClass);
        ReflectionUtils.doWithMethods(fbClass, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                if (method.getName().equals(factoryMethodName) && FactoryBean.class.isAssignableFrom(method.getReturnType())) {
                    Class<?> currentType = GenericTypeResolver.resolveReturnTypeArgument(method, FactoryBean.class);
                    if (currentType != null) {
                        objectType.value = ClassUtils.determineCommonAncestor(currentType, objectType.value);
                    }
                }

            }
        });
        return objectType.value != null && Object.class != objectType.value ? objectType.value : null;
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (bean != null && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            Iterator var5 = this.getBeanPostProcessors().iterator();

            while(var5.hasNext()) {
                BeanPostProcessor bp = (BeanPostProcessor)var5.next();
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
                    if (exposedObject == null) {
                        return null;
                    }
                }
            }
        }

        return exposedObject;
    }

    private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        synchronized(this.getSingletonMutex()) {
            BeanWrapper bw = (BeanWrapper)this.factoryBeanInstanceCache.get(beanName);
            if (bw != null) {
                return (FactoryBean)bw.getWrappedInstance();
            } else {
                Object beanInstance = this.getSingleton(beanName, false);
                if (beanInstance instanceof FactoryBean) {
                    return (FactoryBean)beanInstance;
                } else if (this.isSingletonCurrentlyInCreation(beanName) || mbd.getFactoryBeanName() != null && this.isSingletonCurrentlyInCreation(mbd.getFactoryBeanName())) {
                    return null;
                } else {
                    Object instance;
                    try {
                        this.beforeSingletonCreation(beanName);
                        instance = this.resolveBeforeInstantiation(beanName, mbd);
                        if (instance == null) {
                            bw = this.createBeanInstance(beanName, mbd, (Object[])null);
                            instance = bw.getWrappedInstance();
                        }
                    } finally {
                        this.afterSingletonCreation(beanName);
                    }

                    FactoryBean fb = this.getFactoryBean(beanName, instance);
                    if (bw != null) {
                        this.factoryBeanInstanceCache.put(beanName, bw);
                    }

                    return fb;
                }
            }
        }
    }

    private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        if (this.isPrototypeCurrentlyInCreation(beanName)) {
            return null;
        } else {
            Object instance = null;

            Object var5;
            try {
                this.beforePrototypeCreation(beanName);
                instance = this.resolveBeforeInstantiation(beanName, mbd);
                if (instance == null) {
                    BeanWrapper bw = this.createBeanInstance(beanName, mbd, (Object[])null);
                    instance = bw.getWrappedInstance();
                }

                return this.getFactoryBean(beanName, instance);
            } catch (BeanCreationException var9) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean creation exception on non-singleton FactoryBean type check: " + var9);
                }

                this.onSuppressedException(var9);
                var5 = null;
            } finally {
                this.afterPrototypeCreation(beanName);
            }

            return (FactoryBean)var5;
        }
    }

    protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
        Iterator var4 = this.getBeanPostProcessors().iterator();

        while(var4.hasNext()) {
            BeanPostProcessor bp = (BeanPostProcessor)var4.next();
            if (bp instanceof MergedBeanDefinitionPostProcessor) {
                MergedBeanDefinitionPostProcessor bdp = (MergedBeanDefinitionPostProcessor)bp;
                bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);
            }
        }

    }

    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                Class<?> targetType = this.determineTargetType(beanName, mbd);
                if (targetType != null) {
                    bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                    if (bean != null) {
                        bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
                    }
                }
            }

            mbd.beforeInstantiationResolved = bean != null;
        }

        return bean;
    }

    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        Iterator var3 = this.getBeanPostProcessors().iterator();

        while(var3.hasNext()) {
            BeanPostProcessor bp = (BeanPostProcessor)var3.next();
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        Class<?> beanClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        } else if (mbd.getFactoryMethodName() != null) {
            return this.instantiateUsingFactoryMethod(beanName, mbd, args);
        } else {
            boolean resolved = false;
            boolean autowireNecessary = false;
            if (args == null) {
                synchronized(mbd.constructorArgumentLock) {
                    if (mbd.resolvedConstructorOrFactoryMethod != null) {
                        resolved = true;
                        autowireNecessary = mbd.constructorArgumentsResolved;
                    }
                }
            }

            if (resolved) {
                return autowireNecessary ? this.autowireConstructor(beanName, mbd, (Constructor[])null, (Object[])null) : this.instantiateBean(beanName, mbd);
            } else {
                Constructor<?>[] ctors = this.determineConstructorsFromBeanPostProcessors(beanClass, beanName);
                return ctors == null && mbd.getResolvedAutowireMode() != 3 && !mbd.hasConstructorArgumentValues() && ObjectUtils.isEmpty(args) ? this.instantiateBean(beanName, mbd) : this.autowireConstructor(beanName, mbd, ctors, args);
            }
        }
    }

    protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass != null && this.hasInstantiationAwareBeanPostProcessors()) {
            Iterator var3 = this.getBeanPostProcessors().iterator();

            while(var3.hasNext()) {
                BeanPostProcessor bp = (BeanPostProcessor)var3.next();
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);
                    if (ctors != null) {
                        return ctors;
                    }
                }
            }
        }

        return null;
    }

    protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
        try {
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        return AbstractAutowireCapableBeanFactory.this.getInstantiationStrategy().instantiate(mbd, beanName, AbstractAutowireCapableBeanFactory.this);
                    }
                }, this.getAccessControlContext());
            } else {
                beanInstance = this.getInstantiationStrategy().instantiate(mbd, beanName, this);
            }

            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            this.initBeanWrapper(bw);
            return bw;
        } catch (Throwable var6) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", var6);
        }
    }

    protected BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, Object[] explicitArgs) {
        return (new ConstructorResolver(this)).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
    }

    protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor<?>[] ctors, Object[] explicitArgs) {
        return (new ConstructorResolver(this)).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }

    // 对属性的注入
    protected void populateBean(String beanName, RootBeanDefinition mbd, BeanWrapper bw) {
        PropertyValues pvs = mbd.getPropertyValues();
        if (bw == null) {
            if (!((PropertyValues)pvs).isEmpty()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
        } else {
            boolean continueWithPropertyPopulation = true;
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                Iterator var6 = this.getBeanPostProcessors().iterator();

                while(var6.hasNext()) {
                    BeanPostProcessor bp = (BeanPostProcessor)var6.next();
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;

                        if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                            continueWithPropertyPopulation = false;
                            break;
                        }
                    }
                }
            }

            if (continueWithPropertyPopulation) {
                if (mbd.getResolvedAutowireMode() == 1 || mbd.getResolvedAutowireMode() == 2) {
                    MutablePropertyValues newPvs = new MutablePropertyValues((PropertyValues)pvs);
                    if (mbd.getResolvedAutowireMode() == 1) {
                        this.autowireByName(beanName, mbd, bw, newPvs);
                    }

                    if (mbd.getResolvedAutowireMode() == 2) {
                        this.autowireByType(beanName, mbd, bw, newPvs);
                    }

                    pvs = newPvs;
                }

                boolean hasInstAwareBpps = this.hasInstantiationAwareBeanPostProcessors();
                boolean needsDepCheck = mbd.getDependencyCheck() != 0;
                if (hasInstAwareBpps || needsDepCheck) {
                    PropertyDescriptor[] filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                    if (hasInstAwareBpps) {
                        Iterator var9 = this.getBeanPostProcessors().iterator();

                        while(var9.hasNext()) {
                            BeanPostProcessor bp = (BeanPostProcessor)var9.next();
                            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                                pvs = ibp.postProcessPropertyValues((PropertyValues)pvs, filteredPds, bw.getWrappedInstance(), beanName);
                                if (pvs == null) {
                                    return;
                                }
                            }
                        }
                    }

                    if (needsDepCheck) {
                        this.checkDependencies(beanName, mbd, filteredPds, (PropertyValues)pvs);
                    }
                }

                this.applyPropertyValues(beanName, mbd, bw, (PropertyValues)pvs);
            }
        }
    }

    protected void autowireByName(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        String[] propertyNames = this.unsatisfiedNonSimpleProperties(mbd, bw);
        String[] var6 = propertyNames;
        int var7 = propertyNames.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String propertyName = var6[var8];
            if (this.containsBean(propertyName)) {
                Object bean = this.getBean(propertyName);
                pvs.add(propertyName, bean);
                this.registerDependentBean(propertyName, beanName);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found");
            }
        }

    }

    protected void autowireByType(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        TypeConverter converter = this.getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }

        Set<String> autowiredBeanNames = new LinkedHashSet(4);
        String[] propertyNames = this.unsatisfiedNonSimpleProperties(mbd, bw);
        String[] var8 = propertyNames;
        int var9 = propertyNames.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            String propertyName = var8[var10];

            try {
                PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                if (Object.class != pd.getPropertyType()) {
                    MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                    boolean eager = !PriorityOrdered.class.isAssignableFrom(bw.getWrappedClass());
                    DependencyDescriptor desc = new AbstractAutowireCapableBeanFactory.AutowireByTypeDependencyDescriptor(methodParam, eager);
                    Object autowiredArgument = this.resolveDependency(desc, beanName, autowiredBeanNames, (TypeConverter)converter);
                    if (autowiredArgument != null) {
                        pvs.add(propertyName, autowiredArgument);
                    }

                    Iterator var17 = autowiredBeanNames.iterator();

                    while(var17.hasNext()) {
                        String autowiredBeanName = (String)var17.next();
                        this.registerDependentBean(autowiredBeanName, beanName);
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Autowiring by type from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + autowiredBeanName + "'");
                        }
                    }

                    autowiredBeanNames.clear();
                }
            } catch (BeansException var19) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, var19);
            }
        }

    }

    protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
        Set<String> result = new TreeSet();
        PropertyValues pvs = mbd.getPropertyValues();
        PropertyDescriptor[] pds = bw.getPropertyDescriptors();
        PropertyDescriptor[] var6 = pds;
        int var7 = pds.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            PropertyDescriptor pd = var6[var8];
            if (pd.getWriteMethod() != null && !this.isExcludedFromDependencyCheck(pd) && !pvs.contains(pd.getName()) && !BeanUtils.isSimpleProperty(pd.getPropertyType())) {
                result.add(pd.getName());
            }
        }

        return StringUtils.toStringArray(result);
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw, boolean cache) {
        PropertyDescriptor[] filtered = (PropertyDescriptor[])this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
        if (filtered == null) {
            filtered = this.filterPropertyDescriptorsForDependencyCheck(bw);
            if (cache) {
                PropertyDescriptor[] existing = (PropertyDescriptor[])this.filteredPropertyDescriptorsCache.putIfAbsent(bw.getWrappedClass(), filtered);
                if (existing != null) {
                    filtered = existing;
                }
            }
        }

        return filtered;
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
        List<PropertyDescriptor> pds = new LinkedList(Arrays.asList(bw.getPropertyDescriptors()));
        Iterator it = pds.iterator();

        while(it.hasNext()) {
            PropertyDescriptor pd = (PropertyDescriptor)it.next();
            if (this.isExcludedFromDependencyCheck(pd)) {
                it.remove();
            }
        }

        return (PropertyDescriptor[])pds.toArray(new PropertyDescriptor[pds.size()]);
    }

    protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        return AutowireUtils.isExcludedFromDependencyCheck(pd) || this.ignoredDependencyTypes.contains(pd.getPropertyType()) || AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces);
    }

    protected void checkDependencies(String beanName, AbstractBeanDefinition mbd, PropertyDescriptor[] pds, PropertyValues pvs) throws UnsatisfiedDependencyException {
        int dependencyCheck = mbd.getDependencyCheck();
        PropertyDescriptor[] var6 = pds;
        int var7 = pds.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            PropertyDescriptor pd = var6[var8];
            if (pd.getWriteMethod() != null && !pvs.contains(pd.getName())) {
                boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
                boolean unsatisfied = dependencyCheck == 3 || isSimple && dependencyCheck == 2 || !isSimple && dependencyCheck == 1;
                if (unsatisfied) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(), "Set this property value or disable dependency checking for this bean.");
                }
            }
        }

    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        if (pvs != null && !pvs.isEmpty()) {
            if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
                ((BeanWrapperImpl)bw).setSecurityContext(this.getAccessControlContext());
            }

            MutablePropertyValues mpvs = null;
            List original;
            if (pvs instanceof MutablePropertyValues) {
                mpvs = (MutablePropertyValues)pvs;
                if (mpvs.isConverted()) {
                    try {
                        bw.setPropertyValues(mpvs);
                        return;
                    } catch (BeansException var18) {
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", var18);
                    }
                }

                original = mpvs.getPropertyValueList();
            } else {
                original = Arrays.asList(pvs.getPropertyValues());
            }

            TypeConverter converter = this.getCustomTypeConverter();
            if (converter == null) {
                converter = bw;
            }

            BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, (TypeConverter)converter);
            List<PropertyValue> deepCopy = new ArrayList(original.size());
            boolean resolveNecessary = false;
            Iterator var11 = original.iterator();

            while(true) {
                while(var11.hasNext()) {
                    PropertyValue pv = (PropertyValue)var11.next();
                    if (pv.isConverted()) {
                        deepCopy.add(pv);
                    } else {
                        String propertyName = pv.getName();
                        Object originalValue = pv.getValue();
                        Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                        Object convertedValue = resolvedValue;
                        boolean convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
                        if (convertible) {
                            convertedValue = this.convertForProperty(resolvedValue, propertyName, bw, (TypeConverter)converter);
                        }

                        if (resolvedValue == originalValue) {
                            if (convertible) {
                                pv.setConvertedValue(convertedValue);
                            }

                            deepCopy.add(pv);
                        } else if (convertible && originalValue instanceof TypedStringValue && !((TypedStringValue)originalValue).isDynamic() && !(convertedValue instanceof Collection) && !ObjectUtils.isArray(convertedValue)) {
                            pv.setConvertedValue(convertedValue);
                            deepCopy.add(pv);
                        } else {
                            resolveNecessary = true;
                            deepCopy.add(new PropertyValue(pv, convertedValue));
                        }
                    }
                }

                if (mpvs != null && !resolveNecessary) {
                    mpvs.setConverted();
                }

                try {
                    bw.setPropertyValues(new MutablePropertyValues(deepCopy));
                    return;
                } catch (BeansException var19) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", var19);
                }
            }
        }
    }

    private Object convertForProperty(Object value, String propertyName, BeanWrapper bw, TypeConverter converter) {
        if (converter instanceof BeanWrapperImpl) {
            return ((BeanWrapperImpl)converter).convertForProperty(value, propertyName);
        } else {
            PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
            MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
            return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
        }
    }

    protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    AbstractAutowireCapableBeanFactory.this.invokeAwareMethods(beanName, bean);
                    return null;
                }
            }, this.getAccessControlContext());
        } else {
            this.invokeAwareMethods(beanName, bean);
        }

        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        }

        try {
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Throwable var6) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", var6);
        }

        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }

    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware)bean).setBeanName(beanName);
            }

            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware)bean).setBeanClassLoader(this.getBeanClassLoader());
            }

            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware)bean).setBeanFactory(this);
            }
        }

    }

    protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd) throws Throwable {
        boolean isInitializingBean = bean instanceof InitializingBean;
        if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
            }

            if (System.getSecurityManager() != null) {
                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            ((InitializingBean)bean).afterPropertiesSet();
                            return null;
                        }
                    }, this.getAccessControlContext());
                } catch (PrivilegedActionException var6) {
                    throw var6.getException();
                }
            } else {
                ((InitializingBean)bean).afterPropertiesSet();
            }
        }

        if (mbd != null) {
            String initMethodName = mbd.getInitMethodName();
            if (initMethodName != null && (!isInitializingBean || !"afterPropertiesSet".equals(initMethodName)) && !mbd.isExternallyManagedInitMethod(initMethodName)) {
                this.invokeCustomInitMethod(beanName, bean, mbd);
            }
        }

    }

    protected void invokeCustomInitMethod(String beanName, final Object bean, RootBeanDefinition mbd) throws Throwable {
        String initMethodName = mbd.getInitMethodName();
        final Method initMethod = mbd.isNonPublicAccessAllowed() ? BeanUtils.findMethod(bean.getClass(), initMethodName, new Class[0]) : ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName, new Class[0]);
        if (initMethod == null) {
            if (mbd.isEnforceInitMethod()) {
                throw new BeanDefinitionValidationException("Couldn't find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("No default init method named '" + initMethodName + "' found on bean with name '" + beanName + "'");
                }

            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
            }

            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        ReflectionUtils.makeAccessible(initMethod);
                        return null;
                    }
                });

                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            initMethod.invoke(bean);
                            return null;
                        }
                    }, this.getAccessControlContext());
                } catch (PrivilegedActionException var9) {
                    InvocationTargetException ex = (InvocationTargetException)var9.getException();
                    throw ex.getTargetException();
                }
            } else {
                try {
                    ReflectionUtils.makeAccessible(initMethod);
                    initMethod.invoke(bean);
                } catch (InvocationTargetException var8) {
                    throw var8.getTargetException();
                }
            }

        }
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
        return this.applyBeanPostProcessorsAfterInitialization(object, beanName);
    }

    protected void removeSingleton(String beanName) {
        synchronized(this.getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanInstanceCache.remove(beanName);
        }
    }

    protected void clearSingletonCache() {
        synchronized(this.getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanInstanceCache.clear();
        }
    }

    private static class AutowireByTypeDependencyDescriptor extends DependencyDescriptor {
        public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
            super(methodParameter, false, eager);
        }

        public String getDependencyName() {
            return null;
        }
    }
}
