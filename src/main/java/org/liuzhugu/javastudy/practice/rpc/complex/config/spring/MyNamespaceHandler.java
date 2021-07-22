package org.liuzhugu.javastudy.practice.rpc.complex.config.spring;

import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean.ConsumerBean;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean.ProviderBean;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean.ServerBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MyNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("consumer",new MyBeanDefinitionParser(ConsumerBean.class));
        registerBeanDefinitionParser("provider",new MyBeanDefinitionParser(ProviderBean.class));
        registerBeanDefinitionParser("server",new MyBeanDefinitionParser(ServerBean.class));
    }
}
