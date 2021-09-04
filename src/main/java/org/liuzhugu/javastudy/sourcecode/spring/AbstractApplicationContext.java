package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.*;
import org.springframework.context.support.*;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * mustWatch Spring应用上下文
 * */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext, DisposableBean {
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    protected final Log logger;
    private String id;
    private String displayName;
    private ApplicationContext parent;
    private ConfigurableEnvironment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
    private long startupDate;
    private final AtomicBoolean active;
    private final AtomicBoolean closed;
    private final Object startupShutdownMonitor;
    private Thread shutdownHook;
    private ResourcePatternResolver resourcePatternResolver;
    private LifecycleProcessor lifecycleProcessor;
    private MessageSource messageSource;
    private ApplicationEventMulticaster applicationEventMulticaster;
    private final Set<ApplicationListener<?>> applicationListeners;
    private Set<ApplicationEvent> earlyApplicationEvents;

    public AbstractApplicationContext() {
        this.logger = LogFactory.getLog(this.getClass());
        this.id = ObjectUtils.identityToString(this);
        this.displayName = ObjectUtils.identityToString(this);
        this.beanFactoryPostProcessors = new ArrayList();
        this.active = new AtomicBoolean();
        this.closed = new AtomicBoolean();
        this.startupShutdownMonitor = new Object();
        this.applicationListeners = new LinkedHashSet();
        this.resourcePatternResolver = this.getResourcePatternResolver();
    }

    public AbstractApplicationContext(ApplicationContext parent) {
        this();
        this.setParent(parent);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getApplicationName() {
        return "";
    }

    public void setDisplayName(String displayName) {
        Assert.hasLength(displayName, "Display name must not be empty");
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ApplicationContext getParent() {
        return this.parent;
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }

        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return this.getBeanFactory();
    }

    public long getStartupDate() {
        return this.startupDate;
    }

    public void publishEvent(ApplicationEvent event) {
        this.publishEvent(event, (ResolvableType)null);
    }

    public void publishEvent(Object event) {
        this.publishEvent(event, (ResolvableType)null);
    }

    protected void publishEvent(Object event, ResolvableType eventType) {
        Assert.notNull(event, "Event must not be null");
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Publishing event in " + this.getDisplayName() + ": " + event);
        }

        Object applicationEvent;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent)event;
        } else {
            applicationEvent = new PayloadApplicationEvent(this, event);
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
            }
        }

        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add((ApplicationEvent)applicationEvent);
        } else {
            this.getApplicationEventMulticaster().multicastEvent((ApplicationEvent)applicationEvent, eventType);
        }

        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }

    }

    ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
        } else {
            return this.applicationEventMulticaster;
        }
    }

    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
        } else {
            return this.lifecycleProcessor;
        }
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }

    public void setParent(ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                this.getEnvironment().merge((ConfigurableEnvironment)parentEnvironment);
            }
        }

    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        } else {
            this.applicationListeners.add(listener);
        }

    }

    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }

    /**
     * $1 上下文刷新  也就是重建容器
     * */
    public void refresh() throws BeansException, IllegalStateException {
        synchronized(this.startupShutdownMonitor) {
            //1.预备工作
            this.prepareRefresh();
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            //  1.1 bean工厂的初始化工作
            this.prepareBeanFactory(beanFactory);

            try {
                //2.处理bean工厂
                this.postProcessBeanFactory(beanFactory);
                //3.调用bean工厂的后置处理器
                this.invokeBeanFactoryPostProcessors(beanFactory);
                //4.注册后置处理器   如 AutowiredAnnotationBeanPostProcessor
                this.registerBeanPostProcessors(beanFactory);
                //5.
                this.initMessageSource();
                //6.初始化应用事件的广播
                this.initApplicationEventMulticaster();
                //7. $2 刷新上下文  也就是重构容器
                this.onRefresh();
                //8.注册监听者
                this.registerListeners();
                //9.对所有bean调用getBean   因为都为初始化  那么相当于初始化所有非延迟的bean
                this.finishBeanFactoryInitialization(beanFactory);
                //10.结束刷新
                this.finishRefresh();
            } catch (BeansException var9) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                }

                this.destroyBeans();
                this.cancelRefresh(var9);
                throw var9;
            } finally {
                this.resetCommonCaches();
            }

        }
    }

    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Refreshing " + this);
        }

        this.initPropertySources();
        this.getEnvironment().validateRequiredProperties();
        this.earlyApplicationEvents = new LinkedHashSet();
    }

    protected void initPropertySources() {
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        this.refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Bean factory for " + this.getDisplayName() + ": " + beanFactory);
        }

        return beanFactory;
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(this.getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, this.getEnvironment()));
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
        if (beanFactory.containsBean("loadTimeWeaver")) {
            //beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", this.getEnvironment());
        }

        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", this.getEnvironment().getSystemProperties());
        }

        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", this.getEnvironment().getSystemEnvironment());
        }

    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, this.getBeanFactoryPostProcessors());
        if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean("loadTimeWeaver")) {
            //beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    protected void initMessageSource() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("messageSource")) {
            this.messageSource = (MessageSource)beanFactory.getBean("messageSource", MessageSource.class);
            if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
                HierarchicalMessageSource hms = (HierarchicalMessageSource)this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    hms.setParentMessageSource(this.getInternalParentMessageSource());
                }
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MessageSource [" + this.messageSource + "]");
            }
        } else {
            DelegatingMessageSource dms = new DelegatingMessageSource();
            dms.setParentMessageSource(this.getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton("messageSource", this.messageSource);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate MessageSource with name 'messageSource': using default [" + this.messageSource + "]");
            }
        }

    }

    protected void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
            this.applicationEventMulticaster = (ApplicationEventMulticaster)beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
            }
        } else {
            this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
            beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate ApplicationEventMulticaster with name 'applicationEventMulticaster': using default [" + this.applicationEventMulticaster + "]");
            }
        }

    }

    protected void initLifecycleProcessor() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("lifecycleProcessor")) {
            this.lifecycleProcessor = (LifecycleProcessor)beanFactory.getBean("lifecycleProcessor", LifecycleProcessor.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
            }
        } else {
            DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory(beanFactory);
            this.lifecycleProcessor = defaultProcessor;
            beanFactory.registerSingleton("lifecycleProcessor", this.lifecycleProcessor);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate LifecycleProcessor with name 'lifecycleProcessor': using default [" + this.lifecycleProcessor + "]");
            }
        }

    }

    protected void onRefresh() throws BeansException {
    }

    protected void registerListeners() {
        Iterator var1 = this.getApplicationListeners().iterator();

        while(var1.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var1.next();
            this.getApplicationEventMulticaster().addApplicationListener(listener);
        }

        String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
        String[] var7 = listenerBeanNames;
        int var3 = listenerBeanNames.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String listenerBeanName = var7[var4];
            this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (earlyEventsToProcess != null) {
            Iterator var9 = earlyEventsToProcess.iterator();

            while(var9.hasNext()) {
                ApplicationEvent earlyEvent = (ApplicationEvent)var9.next();
                this.getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }

    }


    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
            beanFactory.setConversionService((ConversionService)beanFactory.getBean("conversionService", ConversionService.class));
        }

        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(new StringValueResolver() {
                public String resolveStringValue(String strVal) {
                    return AbstractApplicationContext.this.getEnvironment().resolvePlaceholders(strVal);
                }
            });
        }

        String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        String[] var3 = weaverAwareNames;
        int var4 = weaverAwareNames.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String weaverAwareName = var3[var5];
            //$ 获取bean  最终调取到bean工程的doGetBean
            this.getBean(weaverAwareName);
        }

        beanFactory.setTempClassLoader((ClassLoader)null);
        beanFactory.freezeConfiguration();
        //初始化单例对象
        beanFactory.preInstantiateSingletons();
    }

    protected void finishRefresh() {
        //初始化生命周期处理器
        this.initLifecycleProcessor();
        //调用处理器刷新生命周期
        this.getLifecycleProcessor().onRefresh();
        //通知监听者上下文刷新成功
        this.publishEvent((ApplicationEvent)(new ContextRefreshedEvent(this)));
        LiveBeansView.registerApplicationContext(this);
    }

    protected void cancelRefresh(BeansException ex) {
        this.active.set(false);
    }

    protected void resetCommonCaches() {
        ReflectionUtils.clearCache();
        ResolvableType.clearCache();
        CachedIntrospectionResults.clearClassLoader(this.getClassLoader());
    }

    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread() {
                public void run() {
                    synchronized(AbstractApplicationContext.this.startupShutdownMonitor) {
                        AbstractApplicationContext.this.doClose();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }

    }

    public void destroy() {
        this.close();
    }

    public void close() {
        synchronized(this.startupShutdownMonitor) {
            this.doClose();
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                } catch (IllegalStateException var4) {
                }
            }

        }
    }

    protected void doClose() {
        if (this.active.get() && this.closed.compareAndSet(false, true)) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Closing " + this);
            }

            LiveBeansView.unregisterApplicationContext(this);

            try {
                this.publishEvent((ApplicationEvent)(new ContextClosedEvent(this)));
            } catch (Throwable var3) {
                this.logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", var3);
            }

            if (this.lifecycleProcessor != null) {
                try {
                    this.lifecycleProcessor.onClose();
                } catch (Throwable var2) {
                    this.logger.warn("Exception thrown from LifecycleProcessor on context close", var2);
                }
            }

            this.destroyBeans();
            this.closeBeanFactory();
            this.onClose();
            this.active.set(false);
        }

    }

    protected void destroyBeans() {
        this.getBeanFactory().destroySingletons();
    }

    protected void onClose() {
    }

    public boolean isActive() {
        return this.active.get();
    }

    protected void assertBeanFactoryActive() {
        if (!this.active.get()) {
            if (this.closed.get()) {
                throw new IllegalStateException(this.getDisplayName() + " has been closed already");
            } else {
                throw new IllegalStateException(this.getDisplayName() + " has not been refreshed yet");
            }
        }
    }

    public Object getBean(String name) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, requiredType);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(requiredType);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, args);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(requiredType, args);
    }

    public boolean containsBean(String name) {
        return this.getBeanFactory().containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isSingleton(name);
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isPrototype(name);
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getType(name);
    }

    public String[] getAliases(String name) {
        return this.getBeanFactory().getAliases(name);
    }

    public boolean containsBeanDefinition(String beanName) {
        return this.getBeanFactory().containsBeanDefinition(beanName);
    }

    public int getBeanDefinitionCount() {
        return this.getBeanFactory().getBeanDefinitionCount();
    }

    public String[] getBeanDefinitionNames() {
        return this.getBeanFactory().getBeanDefinitionNames();
    }

    public String[] getBeanNamesForType(ResolvableType type) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type);
    }

    public String[] getBeanNamesForType(Class<?> type) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type);
    }

    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansWithAnnotation(annotationType);
    }

    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }

    public BeanFactory getParentBeanFactory() {
        return this.getParent();
    }

    public boolean containsLocalBean(String name) {
        return this.getBeanFactory().containsLocalBean(name);
    }

    protected BeanFactory getInternalParentBeanFactory() {
        return (BeanFactory)(this.getParent() instanceof ConfigurableApplicationContext ? ((ConfigurableApplicationContext)this.getParent()).getBeanFactory() : this.getParent());
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return this.getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(code, args, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(resolvable, locale);
    }

    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - call 'refresh' before accessing messages via the context: " + this);
        } else {
            return this.messageSource;
        }
    }

    protected MessageSource getInternalParentMessageSource() {
        return (MessageSource)(this.getParent() instanceof AbstractApplicationContext ? ((AbstractApplicationContext)this.getParent()).messageSource : this.getParent());
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }

    public void start() {
        this.getLifecycleProcessor().start();
        this.publishEvent((ApplicationEvent)(new ContextStartedEvent(this)));
    }

    public void stop() {
        this.getLifecycleProcessor().stop();
        this.publishEvent((ApplicationEvent)(new ContextStoppedEvent(this)));
    }

    public boolean isRunning() {
        return this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning();
    }

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    protected abstract void closeBeanFactory();

    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getDisplayName());
        sb.append(": startup date [").append(new Date(this.getStartupDate()));
        sb.append("]; ");
        ApplicationContext parent = this.getParent();
        if (parent == null) {
            sb.append("root of context hierarchy");
        } else {
            sb.append("parent: ").append(parent.getDisplayName());
        }

        return sb.toString();
    }

    static {
        ContextClosedEvent.class.getName();
    }
}
