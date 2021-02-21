package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;


import org.liuzhugu.javastudy.framestudy.mybatis.mylike.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;

/**
 * 自定义mapper扫描类
 * 扫描包下的接口类   注册mapper
 * */
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    //从哪扫描
    private String basePackage;
    //放到哪去
    private SqlSessionFactory sqlSessionFactory;
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            //1.扫描bean
            //扫描注指定路径下的所有class
            String packageSearchPath = "classpath*:" + basePackage.replace(".","/") + "/**/*.class";
            //根据路径获取所有资源
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(packageSearchPath);

            for (Resource resource : resources) {
                MetadataReader metadataReader = new SimpleMetadataReader(resource, ClassUtils.getDefaultClassLoader());

                ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
                //用于获取字符串并将其转换为普通Java变量
                String beanName = Introspector.decapitalize(ClassUtils.getShortName(beanDefinition.getBeanClassName()));

                //配置bean
                beanDefinition.setResource(resource);
                beanDefinition.setSource(resource);
                beanDefinition.setScope("singleton");
                //设置bean的 id以及值
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(sqlSessionFactory);
                beanDefinition.setBeanClass(MapperFactoryBean.class);

                //2.注册bean
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                registry.registerBeanDefinition(beanName,definitionHolder.getBeanDefinition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // left intentionally blank
    }
}
