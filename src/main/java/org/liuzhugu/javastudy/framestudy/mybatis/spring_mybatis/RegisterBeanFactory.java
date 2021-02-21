package org.liuzhugu.javastudy.framestudy.mybatis.spring_mybatis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

//
public class RegisterBeanFactory implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        //设置代理类工厂
        beanDefinition.setBeanClass(ProxyBeanFactory.class);
        //实现类
        //beanDefinition.setBeanClass(UserDao.class);

        //创建处理类
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, "userDao");
        //将我们的bean注册到spring容器中    key为beanName  value为bean定义
        registry.registerBeanDefinition(definitionHolder.getBeanName(),definitionHolder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // left intentionally blank
    }
}
