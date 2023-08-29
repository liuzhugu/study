package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mustWatch Autowired
 * */
public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet();
    private String requiredParameterName = "required";
    private boolean requiredParameterValue = true;
    private int order = 2147483645;
    private ConfigurableListableBeanFactory beanFactory;
    private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = new ConcurrentHashMap(256);
    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap(256);

    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);

        try {
            this.autowiredAnnotationTypes.add((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
            this.logger.info("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
        } catch (ClassNotFoundException var2) {
        }

    }

    public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
        Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.add(autowiredAnnotationType);
    }

    public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }

    public void setRequiredParameterName(String requiredParameterName) {
        this.requiredParameterName = requiredParameterName;
    }

    public void setRequiredParameterValue(boolean requiredParameterValue) {
        this.requiredParameterValue = requiredParameterValue;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        } else {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = this.findAutowiringMetadata(beanName, beanType, (PropertyValues)null);
            metadata.checkConfigMembers(beanDefinition);
        }

    }

    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName) throws BeanCreationException {
        if (!this.lookupMethodsChecked.contains(beanName)) {
            try {
                ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        Lookup lookup = (Lookup)method.getAnnotation(Lookup.class);
                        if (lookup != null) {
                            LookupOverride override = new LookupOverride(method, lookup.value());

                            try {
                                RootBeanDefinition mbd = (RootBeanDefinition) AutowiredAnnotationBeanPostProcessor.this.beanFactory.getMergedBeanDefinition(beanName);
                                mbd.getMethodOverrides().addOverride(override);
                            } catch (NoSuchBeanDefinitionException var5) {
                                throw new BeanCreationException(beanName, "Cannot apply @Lookup to beans without corresponding bean definition");
                            }
                        }

                    }
                });
            } catch (IllegalStateException var19) {
                throw new BeanCreationException(beanName, "Lookup method resolution failed", var19);
            } catch (NoClassDefFoundError var20) {
                throw new BeanCreationException(beanName, "Failed to introspect bean class [" + beanClass.getName() + "] for lookup method metadata: could not find class that it depends on", var20);
            }

            this.lookupMethodsChecked.add(beanName);
        }

        Constructor<?>[] candidateConstructors = (Constructor[])this.candidateConstructorsCache.get(beanClass);
        if (candidateConstructors == null) {
            synchronized(this.candidateConstructorsCache) {
                candidateConstructors = (Constructor[])this.candidateConstructorsCache.get(beanClass);
                if (candidateConstructors == null) {
                    Constructor[] rawCandidates;
                    try {
                        rawCandidates = beanClass.getDeclaredConstructors();
                    } catch (Throwable var18) {
                        throw new BeanCreationException(beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", var18);
                    }

                    List<Constructor<?>> candidates = new ArrayList(rawCandidates.length);
                    Constructor<?> requiredConstructor = null;
                    Constructor<?> defaultConstructor = null;
                    Constructor[] var9 = rawCandidates;
                    int var10 = rawCandidates.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        Constructor<?> candidate = var9[var11];
                        AnnotationAttributes ann = this.findAutowiredAnnotation(candidate);
                        if (ann == null) {
                            Class<?> userClass = ClassUtils.getUserClass(beanClass);
                            if (userClass != beanClass) {
                                try {
                                    Constructor<?> superCtor = userClass.getDeclaredConstructor(candidate.getParameterTypes());
                                    ann = this.findAutowiredAnnotation(superCtor);
                                } catch (NoSuchMethodException var17) {
                                }
                            }
                        }

                        if (ann != null) {
                            if (requiredConstructor != null) {
                                throw new BeanCreationException(beanName, "Invalid autowire-marked constructor: " + candidate + ". Found constructor with 'required' Autowired annotation already: " + requiredConstructor);
                            }

                            boolean required = this.determineRequiredStatus(ann);
                            if (required) {
                                if (!candidates.isEmpty()) {
                                    throw new BeanCreationException(beanName, "Invalid autowire-marked constructors: " + candidates + ". Found constructor with 'required' Autowired annotation: " + candidate);
                                }

                                requiredConstructor = candidate;
                            }

                            candidates.add(candidate);
                        } else if (candidate.getParameterTypes().length == 0) {
                            defaultConstructor = candidate;
                        }
                    }

                    if (!candidates.isEmpty()) {
                        if (requiredConstructor == null) {
                            if (defaultConstructor != null) {
                                candidates.add(defaultConstructor);
                            } else if (candidates.size() == 1 && this.logger.isWarnEnabled()) {
                                this.logger.warn("Inconsistent constructor declaration on bean with name '" + beanName + "': single autowire-marked constructor flagged as optional - this constructor is effectively required since there is no default constructor to fall back to: " + candidates.get(0));
                            }
                        }

                        candidateConstructors = (Constructor[])candidates.toArray(new Constructor[candidates.size()]);
                    } else if (rawCandidates.length == 1 && rawCandidates[0].getParameterTypes().length > 0) {
                        candidateConstructors = new Constructor[]{rawCandidates[0]};
                    } else {
                        candidateConstructors = new Constructor[0];
                    }

                    this.candidateConstructorsCache.put(beanClass, candidateConstructors);
                }
            }
        }

        return candidateConstructors.length > 0 ? candidateConstructors : null;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {
        InjectionMetadata metadata = this.findAutowiringMetadata(beanName, bean.getClass(), pvs);

        try {
            //￥ 注入属性
            metadata.inject(bean, beanName, pvs);
            return pvs;
        } catch (BeanCreationException var7) {
            throw var7;
        } catch (Throwable var8) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", var8);
        }
    }

    public void processInjection(Object bean) throws BeanCreationException {
        Class<?> clazz = bean.getClass();
        InjectionMetadata metadata = this.findAutowiringMetadata(clazz.getName(), clazz, (PropertyValues)null);

        try {
            metadata.inject(bean, (String)null, (PropertyValues)null);
        } catch (BeanCreationException var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", var6);
        }
    }

    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = (InjectionMetadata)this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized(this.injectionMetadataCache) {
                metadata = (InjectionMetadata)this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }

                    try {
                        metadata = this.buildAutowiringMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError var9) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() + "] for autowiring metadata: could not find class that it depends on", var9);
                    }
                }
            }
        }

        return metadata;
    }

    private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList();
        Class targetClass = clazz;

        do {
            final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList();
            ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    AnnotationAttributes ann = AutowiredAnnotationBeanPostProcessor.this.findAutowiredAnnotation(field);
                    if (ann != null) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            if (AutowiredAnnotationBeanPostProcessor.this.logger.isWarnEnabled()) {
                                AutowiredAnnotationBeanPostProcessor.this.logger.warn("Autowired annotation is not supported on static fields: " + field);
                            }

                            return;
                        }

                        boolean required = AutowiredAnnotationBeanPostProcessor.this.determineRequiredStatus(ann);
                        currElements.add(AutowiredAnnotationBeanPostProcessor.this.new AutowiredFieldElement(field, required));
                    }

                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                    if (BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                        AnnotationAttributes ann = AutowiredAnnotationBeanPostProcessor.this.findAutowiredAnnotation(bridgedMethod);
                        if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                            if (Modifier.isStatic(method.getModifiers())) {
                                if (AutowiredAnnotationBeanPostProcessor.this.logger.isWarnEnabled()) {
                                    AutowiredAnnotationBeanPostProcessor.this.logger.warn("Autowired annotation is not supported on static methods: " + method);
                                }

                                return;
                            }

                            if (method.getParameterTypes().length == 0 && AutowiredAnnotationBeanPostProcessor.this.logger.isWarnEnabled()) {
                                AutowiredAnnotationBeanPostProcessor.this.logger.warn("Autowired annotation should only be used on methods with parameters: " + method);
                            }

                            boolean required = AutowiredAnnotationBeanPostProcessor.this.determineRequiredStatus(ann);
                            PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                            currElements.add(AutowiredAnnotationBeanPostProcessor.this.new AutowiredMethodElement(method, required, pd));
                        }

                    }
                }
            });
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        } while(targetClass != null && targetClass != Object.class);

        return new InjectionMetadata(clazz, elements);
    }

    private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
        if (ao.getAnnotations().length > 0) {
            Iterator var2 = this.autowiredAnnotationTypes.iterator();

            while(var2.hasNext()) {
                Class<? extends Annotation> type = (Class)var2.next();
                AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
                if (attributes != null) {
                    return attributes;
                }
            }
        }

        return null;
    }

    protected boolean determineRequiredStatus(AnnotationAttributes ann) {
        return !ann.containsKey(this.requiredParameterName) || this.requiredParameterValue == ann.getBoolean(this.requiredParameterName);
    }

    protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory configured - override the getBeanOfType method or specify the 'beanFactory' property");
        } else {
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
        }
    }

    private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
        if (beanName != null) {
            Iterator var3 = autowiredBeanNames.iterator();

            while(var3.hasNext()) {
                String autowiredBeanName = (String)var3.next();
                if (this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
                }
            }
        }

    }

    private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor)cachedArgument;
            return this.beanFactory.resolveDependency(descriptor, beanName, (Set)null, (TypeConverter)null);
        } else {
            return cachedArgument;
        }
    }

    private static class ShortcutDependencyDescriptor extends DependencyDescriptor {
        private final String shortcut;
        private final Class<?> requiredType;

        public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
            super(original);
            this.shortcut = shortcut;
            this.requiredType = requiredType;
        }

        public Object resolveShortcut(BeanFactory beanFactory) {
            return this.resolveCandidate(this.shortcut, this.requiredType, beanFactory);
        }
    }

    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {
        private final boolean required;
        private volatile boolean cached = false;
        private volatile Object[] cachedMethodArguments;

        public AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
            super(method, pd);
            this.required = required;
        }

        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            if (!this.checkPropertySkipping(pvs)) {
                Method method = (Method)this.member;
                Object[] arguments;
                if (this.cached) {
                    arguments = this.resolveCachedArguments(beanName);
                } else {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    arguments = new Object[paramTypes.length];
                    DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
                    Set<String> autowiredBeans = new LinkedHashSet(paramTypes.length);
                    TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();

                    for(int ixx = 0; ixx < arguments.length; ++ixx) {
                        MethodParameter methodParam = new MethodParameter(method, ixx);
                        DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
                        currDesc.setContainingClass(bean.getClass());
                        descriptors[ixx] = currDesc;

                        try {
                            Object arg = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
                            if (arg == null && !this.required) {
                                arguments = null;
                                break;
                            }

                            arguments[ixx] = arg;
                        } catch (BeansException var17) {
                            throw new UnsatisfiedDependencyException((String)null, beanName, new InjectionPoint(methodParam), var17);
                        }
                    }

                    synchronized(this) {
                        if (!this.cached) {
                            if (arguments != null) {
                                this.cachedMethodArguments = new Object[paramTypes.length];

                                for(int i = 0; i < arguments.length; ++i) {
                                    this.cachedMethodArguments[i] = descriptors[i];
                                }

                                AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeans);
                                if (autowiredBeans.size() == paramTypes.length) {
                                    Iterator<String> it = autowiredBeans.iterator();

                                    for(int ix = 0; ix < paramTypes.length; ++ix) {
                                        String autowiredBeanName = (String)it.next();
                                        if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, paramTypes[ix])) {
                                            this.cachedMethodArguments[ix] = new AutowiredAnnotationBeanPostProcessor.ShortcutDependencyDescriptor(descriptors[ix], autowiredBeanName, paramTypes[ix]);
                                        }
                                    }
                                }
                            } else {
                                this.cachedMethodArguments = null;
                            }

                            this.cached = true;
                        }
                    }
                }

                if (arguments != null) {
                    try {
                        ReflectionUtils.makeAccessible(method);
                        method.invoke(bean, arguments);
                    } catch (InvocationTargetException var15) {
                        throw var15.getTargetException();
                    }
                }

            }
        }

        private Object[] resolveCachedArguments(String beanName) {
            if (this.cachedMethodArguments == null) {
                return null;
            } else {
                Object[] arguments = new Object[this.cachedMethodArguments.length];

                for(int i = 0; i < arguments.length; ++i) {
                    arguments[i] = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
                }

                return arguments;
            }
        }
    }

    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {
        private final boolean required;
        private volatile boolean cached = false;
        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field, boolean required) {
            super(field, (PropertyDescriptor)null);
            this.required = required;
        }

        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Field field = (Field)this.member;
            Object value;
            if (this.cached) {
                value = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedFieldValue);
            } else {
                DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
                desc.setContainingClass(bean.getClass());
                Set<String> autowiredBeanNames = new LinkedHashSet(1);
                TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();

                try {
                    value = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                } catch (BeansException var12) {
                    throw new UnsatisfiedDependencyException((String)null, beanName, new InjectionPoint(field), var12);
                }

                synchronized(this) {
                    if (!this.cached) {
                        if (value == null && !this.required) {
                            this.cachedFieldValue = null;
                        } else {
                            this.cachedFieldValue = desc;
                            AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                            if (autowiredBeanNames.size() == 1) {
                                String autowiredBeanName = (String)autowiredBeanNames.iterator().next();
                                if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                                    this.cachedFieldValue = new AutowiredAnnotationBeanPostProcessor.ShortcutDependencyDescriptor(desc, autowiredBeanName, field.getType());
                                }
                            }
                        }

                        this.cached = true;
                    }
                }
            }

            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                //￥ 设置属性
                field.set(bean, value);
            }

        }
    }
}
