package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.*;

import java.beans.PropertyEditor;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    private BeanFactory parentBeanFactory;
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private ClassLoader tempClassLoader;
    private boolean cacheBeanMetadata = true;
    private BeanExpressionResolver beanExpressionResolver;
    private ConversionService conversionService;
    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet(4);
    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap(4);
    private TypeConverter typeConverter;
    private final List<StringValueResolver> embeddedValueResolvers = new LinkedList();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList();
    private boolean hasInstantiationAwareBeanPostProcessors;
    private boolean hasDestructionAwareBeanPostProcessors;
    private final Map<String, Scope> scopes = new LinkedHashMap(8);
    private SecurityContextProvider securityContextProvider;
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap(256);
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new NamedThreadLocal("Prototype beans currently in creation");

    public AbstractBeanFactory() {
    }

    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    public Object getBean(String name) throws BeansException {
        return this.doGetBean(name, (Class)null, (Object[])null, false);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return this.doGetBean(name, requiredType, (Object[])null, false);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return this.doGetBean(name, (Class)null, args, false);
    }

    public <T> T getBean(String name, Class<T> requiredType, Object... args) throws BeansException {
        return this.doGetBean(name, requiredType, args, false);
    }

    //获取bean
    protected <T> T doGetBean(String name, Class<T> requiredType, final Object[] args, boolean typeCheckOnly) throws BeansException {
        final String beanName = this.transformedBeanName(name);
        //从单例map中获取
        Object sharedInstance = this.getSingleton(beanName);
        Object bean;
        if (sharedInstance != null && args == null) {

            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, (RootBeanDefinition)null);
        } else {
            if (this.isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }
            //从上一级beanFactory获取
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                String nameToLookup = this.originalBeanName(name);
                if (args != null) {
                    return (T)parentBeanFactory.getBean(nameToLookup, args);
                }

                return parentBeanFactory.getBean(nameToLookup, requiredType);
            }

            if (!typeCheckOnly) {
                this.markBeanAsCreated(beanName);
            }

            try {
                //获取bean的定义
                final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                this.checkMergedBeanDefinition(mbd, beanName, args);
                String[] dependsOn = mbd.getDependsOn();
                String[] var11;
                if (dependsOn != null) {
                    var11 = dependsOn;
                    int var12 = dependsOn.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        String dep = var11[var13];
                        if (this.isDependent(beanName, dep)) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                        }

                        this.registerDependentBean(dep, beanName);

                        try {
                            this.getBean(dep);
                        } catch (NoSuchBeanDefinitionException var24) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "'" + beanName + "' depends on missing bean '" + dep + "'", var24);
                        }
                    }
                }

                if (mbd.isSingleton()) {
                    //获取单例
                    sharedInstance = this.getSingleton(beanName, new ObjectFactory<Object>() {
                        public Object getObject() throws BeansException {
                            try {
                                return AbstractBeanFactory.this.createBean(beanName, mbd, args);
                            } catch (BeansException var2) {
                                AbstractBeanFactory.this.destroySingleton(beanName);
                                throw var2;
                            }
                        }
                    });
                    bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                } else if (mbd.isPrototype()) {
                    var11 = null;

                    Object prototypeInstance;
                    try {
                        this.beforePrototypeCreation(beanName);
                        prototypeInstance = this.createBean(beanName, mbd, args);
                    } finally {
                        this.afterPrototypeCreation(beanName);
                    }

                    bean = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                } else {
                    String scopeName = mbd.getScope();
                    Scope scope = (Scope)this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }

                    try {
                        Object scopedInstance = scope.get(beanName, new ObjectFactory<Object>() {
                            public Object getObject() throws BeansException {
                                AbstractBeanFactory.this.beforePrototypeCreation(beanName);

                                Object var1;
                                try {
                                    var1 = AbstractBeanFactory.this.createBean(beanName, mbd, args);
                                } finally {
                                    AbstractBeanFactory.this.afterPrototypeCreation(beanName);
                                }

                                return var1;
                            }
                        });
                        bean = this.getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    } catch (IllegalStateException var23) {
                        throw new BeanCreationException(beanName, "Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", var23);
                    }
                }
            } catch (BeansException var26) {
                this.cleanupAfterBeanCreationFailure(beanName);
                throw var26;
            }
        }

        if (requiredType != null && bean != null && !requiredType.isInstance(bean)) {
            try {
                return this.getTypeConverter().convertIfNecessary(bean, requiredType);
            } catch (TypeMismatchException var25) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to convert bean '" + name + "' to required type '" + ClassUtils.getQualifiedName(requiredType) + "'", var25);
                }

                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        } else {
            return (T)bean;
        }
    }

    public boolean containsBean(String name) {
        String beanName = this.transformedBeanName(name);
        if (!this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            return parentBeanFactory != null && parentBeanFactory.containsBean(this.originalBeanName(name));
        } else {
            return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(name);
        }
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            if (!(beanInstance instanceof FactoryBean)) {
                return !BeanFactoryUtils.isFactoryDereference(name);
            } else {
                return BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean)beanInstance).isSingleton();
            }
        } else if (this.containsSingleton(beanName)) {
            return true;
        } else {
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.isSingleton(this.originalBeanName(name));
            } else {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                if (mbd.isSingleton()) {
                    if (this.isFactoryBean(beanName, mbd)) {
                        if (BeanFactoryUtils.isFactoryDereference(name)) {
                            return true;
                        } else {
                            FactoryBean<?> factoryBean = (FactoryBean)this.getBean("&" + beanName);
                            return factoryBean.isSingleton();
                        }
                    } else {
                        return !BeanFactoryUtils.isFactoryDereference(name);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.isPrototype(this.originalBeanName(name));
        } else {
            RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            if (mbd.isPrototype()) {
                return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(beanName, mbd);
            } else if (BeanFactoryUtils.isFactoryDereference(name)) {
                return false;
            } else if (this.isFactoryBean(beanName, mbd)) {
                final FactoryBean<?> fb = (FactoryBean)this.getBean("&" + beanName);
                if (System.getSecurityManager() != null) {
                    return (Boolean) AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                        public Boolean run() {
                            return fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype() || !fb.isSingleton();
                        }
                    }, this.getAccessControlContext());
                } else {
                    return fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype() || !fb.isSingleton();
                }
            } else {
                return false;
            }
        }
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        Class classToMatch;
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean) {
                if (BeanFactoryUtils.isFactoryDereference(name)) {
                    return typeToMatch.isInstance(beanInstance);
                } else {
                    Class<?> type = this.getTypeForFactoryBean((FactoryBean)beanInstance);
                    return type != null && typeToMatch.isAssignableFrom(type);
                }
            } else {
                if (!BeanFactoryUtils.isFactoryDereference(name)) {
                    if (typeToMatch.isInstance(beanInstance)) {
                        return true;
                    }

                    if (typeToMatch.hasGenerics() && this.containsBeanDefinition(beanName)) {
                        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                        Class<?> targetType = mbd.getTargetType();
                        if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance) && typeToMatch.isAssignableFrom(targetType)) {
                            classToMatch = typeToMatch.resolve();
                            return classToMatch == null || classToMatch.isInstance(beanInstance);
                        }
                    }
                }

                return false;
            }
        } else if (this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            return false;
        } else {
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.isTypeMatch(this.originalBeanName(name), typeToMatch);
            } else {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                classToMatch = typeToMatch.resolve();
                if (classToMatch == null) {
                    classToMatch = FactoryBean.class;
                }

                Class<?>[] typesToMatch = FactoryBean.class == classToMatch ? new Class[]{classToMatch} : new Class[]{FactoryBean.class, classToMatch};
                BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
                if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
                    RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
                    Class<?> targetClass = this.predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
                    if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                        return typeToMatch.isAssignableFrom(targetClass);
                    }
                }

                Class<?> beanType = this.predictBeanType(beanName, mbd, typesToMatch);
                if (beanType == null) {
                    return false;
                } else {
                    if (FactoryBean.class.isAssignableFrom(beanType)) {
                        if (!BeanFactoryUtils.isFactoryDereference(name)) {
                            beanType = this.getTypeForFactoryBean(beanName, mbd);
                            if (beanType == null) {
                                return false;
                            }
                        }
                    } else if (BeanFactoryUtils.isFactoryDereference(name)) {
                        beanType = this.predictBeanType(beanName, mbd, FactoryBean.class);
                        if (beanType == null || !FactoryBean.class.isAssignableFrom(beanType)) {
                            return false;
                        }
                    }

                    ResolvableType resolvableType = mbd.targetType;
                    if (resolvableType == null) {
                        resolvableType = mbd.factoryMethodReturnType;
                    }

                    if (resolvableType != null && resolvableType.resolve() == beanType) {
                        return typeToMatch.isAssignableFrom(resolvableType);
                    } else {
                        return typeToMatch.isAssignableFrom(beanType);
                    }
                }
            }
        }
    }

    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return this.isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name) ? this.getTypeForFactoryBean((FactoryBean)beanInstance) : beanInstance.getClass();
        } else if (this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            return null;
        } else {
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.getType(this.originalBeanName(name));
            } else {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
                if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
                    RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
                    Class<?> targetClass = this.predictBeanType(dbd.getBeanName(), tbd);
                    if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                        return targetClass;
                    }
                }

                Class<?> beanClass = this.predictBeanType(beanName, mbd);
                if (beanClass != null && FactoryBean.class.isAssignableFrom(beanClass)) {
                    return !BeanFactoryUtils.isFactoryDereference(name) ? this.getTypeForFactoryBean(beanName, mbd) : beanClass;
                } else {
                    return !BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null;
                }
            }
        }
    }

    public String[] getAliases(String name) {
        String beanName = this.transformedBeanName(name);
        List<String> aliases = new ArrayList();
        boolean factoryPrefix = name.startsWith("&");
        String fullBeanName = beanName;
        if (factoryPrefix) {
            fullBeanName = "&" + beanName;
        }

        if (!fullBeanName.equals(name)) {
            aliases.add(fullBeanName);
        }

        String[] retrievedAliases = super.getAliases(beanName);
        String[] var7 = retrievedAliases;
        int var8 = retrievedAliases.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String retrievedAlias = var7[var9];
            String alias = (factoryPrefix ? "&" : "") + retrievedAlias;
            if (!alias.equals(name)) {
                aliases.add(alias);
            }
        }

        if (!this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null) {
                aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
            }
        }

        return StringUtils.toStringArray(aliases);
    }

    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    public boolean containsLocalBean(String name) {
        String beanName = this.transformedBeanName(name);
        return (this.containsSingleton(beanName) || this.containsBeanDefinition(beanName)) && (!BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(beanName));
    }

    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
            throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
        } else {
            this.parentBeanFactory = parentBeanFactory;
        }
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader();
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void setTempClassLoader(ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }

    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }

    public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
        this.cacheBeanMetadata = cacheBeanMetadata;
    }

    public boolean isCacheBeanMetadata() {
        return this.cacheBeanMetadata;
    }

    public void setBeanExpressionResolver(BeanExpressionResolver resolver) {
        this.beanExpressionResolver = resolver;
    }

    public BeanExpressionResolver getBeanExpressionResolver() {
        return this.beanExpressionResolver;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
        this.propertyEditorRegistrars.add(registrar);
    }

    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        Assert.notNull(requiredType, "Required type must not be null");
        Assert.notNull(propertyEditorClass, "PropertyEditor class must not be null");
        this.customEditors.put(requiredType, propertyEditorClass);
    }

    public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
        this.registerCustomEditors(registry);
    }

    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return this.customEditors;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = this.getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        } else {
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            typeConverter.setConversionService(this.getConversionService());
            this.registerCustomEditors(typeConverter);
            return typeConverter;
        }
    }

    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.embeddedValueResolvers.add(valueResolver);
    }

    public boolean hasEmbeddedValueResolver() {
        return !this.embeddedValueResolvers.isEmpty();
    }

    public String resolveEmbeddedValue(String value) {
        if (value == null) {
            return null;
        } else {
            String result = value;
            Iterator var3 = this.embeddedValueResolvers.iterator();

            do {
                if (!var3.hasNext()) {
                    return result;
                }

                StringValueResolver resolver = (StringValueResolver)var3.next();
                result = resolver.resolveStringValue(result);
            } while(result != null);

            return null;
        }
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
        if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
            this.hasInstantiationAwareBeanPostProcessors = true;
        }

        if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
            this.hasDestructionAwareBeanPostProcessors = true;
        }

    }

    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return this.hasInstantiationAwareBeanPostProcessors;
    }

    protected boolean hasDestructionAwareBeanPostProcessors() {
        return this.hasDestructionAwareBeanPostProcessors;
    }

    public void registerScope(String scopeName, Scope scope) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        Assert.notNull(scope, "Scope must not be null");
        if (!"singleton".equals(scopeName) && !"prototype".equals(scopeName)) {
            Scope previous = (Scope)this.scopes.put(scopeName, scope);
            if (previous != null && previous != scope) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]");
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Registering scope '" + scopeName + "' with implementation [" + scope + "]");
            }

        } else {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
    }

    public String[] getRegisteredScopeNames() {
        return StringUtils.toStringArray(this.scopes.keySet());
    }

    public Scope getRegisteredScope(String scopeName) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        return (Scope)this.scopes.get(scopeName);
    }

    public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }

    public AccessControlContext getAccessControlContext() {
        return this.securityContextProvider != null ? this.securityContextProvider.getAccessControlContext() : AccessController.getContext();
    }

    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        Assert.notNull(otherFactory, "BeanFactory must not be null");
        this.setBeanClassLoader(otherFactory.getBeanClassLoader());
        this.setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
        this.setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
        this.setConversionService(otherFactory.getConversionService());
        if (otherFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory)otherFactory;
            this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
            this.customEditors.putAll(otherAbstractFactory.customEditors);
            this.typeConverter = otherAbstractFactory.typeConverter;
            this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
            this.hasInstantiationAwareBeanPostProcessors = this.hasInstantiationAwareBeanPostProcessors || otherAbstractFactory.hasInstantiationAwareBeanPostProcessors;
            this.hasDestructionAwareBeanPostProcessors = this.hasDestructionAwareBeanPostProcessors || otherAbstractFactory.hasDestructionAwareBeanPostProcessors;
            this.scopes.putAll(otherAbstractFactory.scopes);
            this.securityContextProvider = otherAbstractFactory.securityContextProvider;
        } else {
            this.setTypeConverter(otherFactory.getTypeConverter());
            String[] otherScopeNames = otherFactory.getRegisteredScopeNames();
            String[] var3 = otherScopeNames;
            int var4 = otherScopeNames.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String scopeName = var3[var5];
                this.scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
            }
        }

    }

    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        String beanName = this.transformedBeanName(name);
        return (BeanDefinition)(!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.getParentBeanFactory()).getMergedBeanDefinition(beanName) : this.getMergedLocalBeanDefinition(beanName));
    }

    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean;
        } else if (this.containsSingleton(beanName)) {
            return false;
        } else {
            return !this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.getParentBeanFactory()).isFactoryBean(name) : this.isFactoryBean(beanName, this.getMergedLocalBeanDefinition(beanName));
        }
    }

    public boolean isActuallyInCreation(String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName) || this.isPrototypeCurrentlyInCreation(beanName);
    }

    protected boolean isPrototypeCurrentlyInCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        return curVal != null && (curVal.equals(beanName) || curVal instanceof Set && ((Set)curVal).contains(beanName));
    }

    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet(2);
            beanNameSet.add((String)curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set)curVal;
            beanNameSet.add(beanName);
        }

    }

    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set)curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }

    }

    public void destroyBean(String beanName, Object beanInstance) {
        this.destroyBean(beanName, beanInstance, this.getMergedLocalBeanDefinition(beanName));
    }

    protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
        (new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessors(), this.getAccessControlContext())).destroy();
    }

    public void destroyScopedBean(String beanName) {
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (!mbd.isSingleton() && !mbd.isPrototype()) {
            String scopeName = mbd.getScope();
            Scope scope = (Scope)this.scopes.get(scopeName);
            if (scope == null) {
                throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
            } else {
                Object bean = scope.remove(beanName);
                if (bean != null) {
                    this.destroyBean(beanName, bean, mbd);
                }

            }
        } else {
            throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
        }
    }

    protected String transformedBeanName(String name) {
        return this.canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    protected String originalBeanName(String name) {
        String beanName = this.transformedBeanName(name);
        if (name.startsWith("&")) {
            beanName = "&" + beanName;
        }

        return beanName;
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        bw.setConversionService(this.getConversionService());
        this.registerCustomEditors(bw);
    }

    protected void registerCustomEditors(PropertyEditorRegistry registry) {
        PropertyEditorRegistrySupport registrySupport = registry instanceof PropertyEditorRegistrySupport ? (PropertyEditorRegistrySupport)registry : null;
        if (registrySupport != null) {
            registrySupport.useConfigValueEditors();
        }

        Iterator var3;
        if (!this.propertyEditorRegistrars.isEmpty()) {
            var3 = this.propertyEditorRegistrars.iterator();

            while(var3.hasNext()) {
                PropertyEditorRegistrar registrar = (PropertyEditorRegistrar)var3.next();

                try {
                    registrar.registerCustomEditors(registry);
                } catch (BeanCreationException var8) {
                    Throwable rootCause = var8.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        BeanCreationException bce = (BeanCreationException)rootCause;
                        if (this.isCurrentlyInCreation(bce.getBeanName())) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + var8.getBeanName() + "': " + var8.getMessage());
                            }

                            this.onSuppressedException(var8);
                            continue;
                        }
                    }

                    throw var8;
                }
            }
        }

        if (!this.customEditors.isEmpty()) {
            var3 = this.customEditors.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<Class<?>, Class<? extends PropertyEditor>> entry = (Map.Entry)var3.next();
                Class<?> requiredType = (Class)entry.getKey();
                Class<? extends PropertyEditor> editorClass = (Class)entry.getValue();
                registry.registerCustomEditor(requiredType, (PropertyEditor)BeanUtils.instantiateClass(editorClass));
            }
        }

    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        RootBeanDefinition mbd = (RootBeanDefinition)this.mergedBeanDefinitions.get(beanName);
        return mbd != null ? mbd : this.getMergedBeanDefinition(beanName, this.getBeanDefinition(beanName));
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
        return this.getMergedBeanDefinition(beanName, bd, (BeanDefinition)null);
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd, BeanDefinition containingBd) throws BeanDefinitionStoreException {
        synchronized(this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;
            if (containingBd == null) {
                mbd = (RootBeanDefinition)this.mergedBeanDefinitions.get(beanName);
            }

            if (mbd == null) {
                if (bd.getParentName() == null) {
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition)bd).cloneBeanDefinition();
                    } else {
                        mbd = new RootBeanDefinition(bd);
                    }
                } else {
                    BeanDefinition pbd;
                    try {
                        String parentBeanName = this.transformedBeanName(bd.getParentName());
                        if (!beanName.equals(parentBeanName)) {
                            pbd = this.getMergedBeanDefinition(parentBeanName);
                        } else {
                            BeanFactory parent = this.getParentBeanFactory();
                            if (!(parent instanceof ConfigurableBeanFactory)) {
                                throw new NoSuchBeanDefinitionException(parentBeanName, "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName + "': cannot be resolved without an AbstractBeanFactory parent");
                            }

                            pbd = ((ConfigurableBeanFactory)parent).getMergedBeanDefinition(parentBeanName);
                        }
                    } catch (NoSuchBeanDefinitionException var10) {
                        throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, "Could not resolve parent bean definition '" + bd.getParentName() + "'", var10);
                    }

                    mbd = new RootBeanDefinition(pbd);
                    mbd.overrideFrom(bd);
                }

                if (!StringUtils.hasLength(mbd.getScope())) {
                    mbd.setScope("singleton");
                }

                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }

                if (containingBd == null && this.isCacheBeanMetadata()) {
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }

            return mbd;
        }
    }

    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, Object[] args) throws BeanDefinitionStoreException {
        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException(beanName);
        }
    }

    protected void clearMergedBeanDefinition(String beanName) {
        this.mergedBeanDefinitions.remove(beanName);
    }

    public void clearMetadataCache() {
        Iterator mergedBeans = this.mergedBeanDefinitions.keySet().iterator();

        while(mergedBeans.hasNext()) {
            if (!this.isBeanEligibleForMetadataCaching((String)mergedBeans.next())) {
                mergedBeans.remove();
            }
        }

    }

    protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, String beanName, final Class<?>... typesToMatch) throws CannotLoadBeanClassException {
        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            } else {
                return System.getSecurityManager() != null ? (Class)AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                    public Class<?> run() throws Exception {
                        return AbstractBeanFactory.this.doResolveBeanClass(mbd, typesToMatch);
                    }
                }, this.getAccessControlContext()) : this.doResolveBeanClass(mbd, typesToMatch);
            }
        } catch (PrivilegedActionException var6) {
            ClassNotFoundException ex = (ClassNotFoundException)var6.getException();
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        } catch (ClassNotFoundException var7) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), var7);
        } catch (LinkageError var8) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), var8);
        }
    }

    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch) throws ClassNotFoundException {
        ClassLoader beanClassLoader = this.getBeanClassLoader();
        ClassLoader classLoaderToUse = beanClassLoader;
        if (!ObjectUtils.isEmpty(typesToMatch)) {
            ClassLoader tempClassLoader = this.getTempClassLoader();
            if (tempClassLoader != null) {
                classLoaderToUse = tempClassLoader;
                if (tempClassLoader instanceof DecoratingClassLoader) {
                    DecoratingClassLoader dcl = (DecoratingClassLoader)tempClassLoader;
                    Class[] var7 = typesToMatch;
                    int var8 = typesToMatch.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        Class<?> typeToMatch = var7[var9];
                        dcl.excludeClass(typeToMatch.getName());
                    }
                }
            }
        }

        String className = mbd.getBeanClassName();
        if (className != null) {
            Object evaluated = this.evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                if (evaluated instanceof Class) {
                    return (Class)evaluated;
                }

                if (evaluated instanceof String) {
                    return ClassUtils.forName((String)evaluated, classLoaderToUse);
                }

                throw new IllegalStateException("Invalid class name expression result: " + evaluated);
            }

            if (classLoaderToUse != beanClassLoader) {
                return ClassUtils.forName(className, classLoaderToUse);
            }
        }

        return mbd.resolveBeanClass(beanClassLoader);
    }

    protected Object evaluateBeanDefinitionString(String value, BeanDefinition beanDefinition) {
        if (this.beanExpressionResolver == null) {
            return value;
        } else {
            Scope scope = beanDefinition != null ? this.getRegisteredScope(beanDefinition.getScope()) : null;
            return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
        }
    }

    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        } else {
            return mbd.getFactoryMethodName() != null ? null : this.resolveBeanClass(mbd, beanName, typesToMatch);
        }
    }

    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Class<?> beanType = this.predictBeanType(beanName, mbd, FactoryBean.class);
        return beanType != null && FactoryBean.class.isAssignableFrom(beanType);
    }

    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        if (!mbd.isSingleton()) {
            return null;
        } else {
            try {
                FactoryBean<?> factoryBean = (FactoryBean)this.doGetBean("&" + beanName, FactoryBean.class, (Object[])null, true);
                return this.getTypeForFactoryBean(factoryBean);
            } catch (BeanCreationException var4) {
                if (var4.contains(BeanCurrentlyInCreationException.class)) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Bean currently in creation on FactoryBean type check: " + var4);
                    }
                } else if (mbd.isLazyInit()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Bean creation exception on lazy FactoryBean type check: " + var4);
                    }
                } else if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Bean creation exception on non-lazy FactoryBean type check: " + var4);
                }

                this.onSuppressedException(var4);
                return null;
            }
        }
    }

    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            synchronized(this.mergedBeanDefinitions) {
                if (!this.alreadyCreated.contains(beanName)) {
                    this.clearMergedBeanDefinition(beanName);
                    this.alreadyCreated.add(beanName);
                }
            }
        }

    }

    protected void cleanupAfterBeanCreationFailure(String beanName) {
        synchronized(this.mergedBeanDefinitions) {
            this.alreadyCreated.remove(beanName);
        }
    }

    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.alreadyCreated.contains(beanName);
    }

    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            this.removeSingleton(beanName);
            return true;
        } else {
            return false;
        }
    }

    protected boolean hasBeanCreationStarted() {
        return !this.alreadyCreated.isEmpty();
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {
        if (BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(this.transformedBeanName(name), beanInstance.getClass());
        } else if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
            Object object = null;
            if (mbd == null) {
                object = this.getCachedObjectForFactoryBean(beanName);
            }

            if (object == null) {
                FactoryBean<?> factory = (FactoryBean)beanInstance;
                if (mbd == null && this.containsBeanDefinition(beanName)) {
                    mbd = this.getMergedLocalBeanDefinition(beanName);
                }

                boolean synthetic = mbd != null && mbd.isSynthetic();
                object = this.getObjectFromFactoryBean(factory, beanName, !synthetic);
            }

            return object;
        } else {
            return beanInstance;
        }
    }

    public boolean isBeanNameInUse(String beanName) {
        return this.isAlias(beanName) || this.containsLocalBean(beanName) || this.hasDependentBean(beanName);
    }

    protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
        return bean != null && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || this.hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(bean, this.getBeanPostProcessors()));
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
        AccessControlContext acc = System.getSecurityManager() != null ? this.getAccessControlContext() : null;
        if (!mbd.isPrototype() && this.requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                this.registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessors(), acc));
            } else {
                Scope scope = (Scope)this.scopes.get(mbd.getScope());
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
                }

                scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessors(), acc));
            }
        }

    }

    protected abstract boolean containsBeanDefinition(String var1);

    protected abstract BeanDefinition getBeanDefinition(String var1) throws BeansException;

    protected abstract Object createBean(String var1, RootBeanDefinition var2, Object[] var3) throws BeanCreationException;
}
