package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void setParentBeanFactory(BeanFactory var1) throws IllegalStateException;

    void setBeanClassLoader(ClassLoader var1);

    ClassLoader getBeanClassLoader();

    void setTempClassLoader(ClassLoader var1);

    ClassLoader getTempClassLoader();

    void setCacheBeanMetadata(boolean var1);

    boolean isCacheBeanMetadata();

    void setBeanExpressionResolver(BeanExpressionResolver var1);

    BeanExpressionResolver getBeanExpressionResolver();

    void setConversionService(ConversionService var1);

    ConversionService getConversionService();

    void addPropertyEditorRegistrar(PropertyEditorRegistrar var1);

    void registerCustomEditor(Class<?> var1, Class<? extends PropertyEditor> var2);

    void copyRegisteredEditorsTo(PropertyEditorRegistry var1);

    void setTypeConverter(TypeConverter var1);

    TypeConverter getTypeConverter();

    void addEmbeddedValueResolver(StringValueResolver var1);

    boolean hasEmbeddedValueResolver();

    String resolveEmbeddedValue(String var1);

    void addBeanPostProcessor(BeanPostProcessor var1);

    int getBeanPostProcessorCount();

    void registerScope(String var1, Scope var2);

    String[] getRegisteredScopeNames();

    Scope getRegisteredScope(String var1);

    AccessControlContext getAccessControlContext();

    void copyConfigurationFrom(ConfigurableBeanFactory var1);

    void registerAlias(String var1, String var2) throws BeanDefinitionStoreException;

    void resolveAliases(StringValueResolver var1);

    BeanDefinition getMergedBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    boolean isFactoryBean(String var1) throws NoSuchBeanDefinitionException;

    void setCurrentlyInCreation(String var1, boolean var2);

    boolean isCurrentlyInCreation(String var1);

    void registerDependentBean(String var1, String var2);

    String[] getDependentBeans(String var1);

    String[] getDependenciesForBean(String var1);

    void destroyBean(String var1, Object var2);

    void destroyScopedBean(String var1);

    void destroySingletons();
}
