package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public abstract class AopConfigUtils {
    public static final String AUTO_PROXY_CREATOR_BEAN_NAME = "org.springframework.aop.config.internalAutoProxyCreator";
    private static final List<Class<?>> APC_PRIORITY_LIST = new ArrayList();

    public AopConfigUtils() {
    }

    public static BeanDefinition registerAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
        return registerAutoProxyCreatorIfNecessary(registry, (Object)null);
    }

    public static BeanDefinition registerAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, Object source) {
        return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
    }

    public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
        return registerAspectJAutoProxyCreatorIfNecessary(registry, (Object)null);
    }

    public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, Object source) {
        return registerOrEscalateApcAsRequired(AspectJAwareAdvisorAutoProxyCreator.class, registry, source);
    }

    public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
        return registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry, (Object)null);
    }

    public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, Object source) {
        return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
    }

    public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
        }

    }

    public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
        }

    }

    private static BeanDefinition registerOrEscalateApcAsRequired(Class<?> cls, BeanDefinitionRegistry registry, Object source) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            BeanDefinition apcDefinition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
                int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
                int requiredPriority = findPriorityForClass(cls);
                if (currentPriority < requiredPriority) {
                    apcDefinition.setBeanClassName(cls.getName());
                }
            }

            return null;
        } else {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
            beanDefinition.setSource(source);
            beanDefinition.getPropertyValues().add("order", -2147483648);
            beanDefinition.setRole(2);
            registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", beanDefinition);
            return beanDefinition;
        }
    }

    private static int findPriorityForClass(Class<?> clazz) {
        return APC_PRIORITY_LIST.indexOf(clazz);
    }

    private static int findPriorityForClass(String className) {
        for(int i = 0; i < APC_PRIORITY_LIST.size(); ++i) {
            Class<?> clazz = (Class)APC_PRIORITY_LIST.get(i);
            if (clazz.getName().equals(className)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Class name [" + className + "] is not a known auto-proxy creator class");
    }

    static {
        APC_PRIORITY_LIST.add(InfrastructureAdvisorAutoProxyCreator.class);
        APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);
        APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);
    }
}