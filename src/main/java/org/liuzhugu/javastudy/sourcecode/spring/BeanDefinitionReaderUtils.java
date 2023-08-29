package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class BeanDefinitionReaderUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    public BeanDefinitionReaderUtils() {
    }

    public static AbstractBeanDefinition createBeanDefinition(String parentName, String className, ClassLoader classLoader) throws ClassNotFoundException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setParentName(parentName);
        if (className != null) {
            if (classLoader != null) {
                bd.setBeanClass(ClassUtils.forName(className, classLoader));
            } else {
                bd.setBeanClassName(className);
            }
        }

        return bd;
    }

    public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        return generateBeanName(beanDefinition, registry, false);
    }

    public static String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            } else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }

        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        } else {
            String id = generatedBeanName;
            if (isInnerBean) {
                id = generatedBeanName + "#" + ObjectUtils.getIdentityHexString(definition);
            } else {
                for(int counter = -1; counter == -1 || registry.containsBeanDefinition(id); id = generatedBeanName + "#" + counter) {
                    ++counter;
                }
            }

            return id;
        }
    }

    public static void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        String beanName = definitionHolder.getBeanName();
        registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            String[] var4 = aliases;
            int var5 = aliases.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String alias = var4[var6];
                registry.registerAlias(beanName, alias);
            }
        }

    }

    public static String registerWithGeneratedName(AbstractBeanDefinition definition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        String generatedName = generateBeanName(definition, registry, false);
        registry.registerBeanDefinition(generatedName, definition);
        return generatedName;
    }
}
