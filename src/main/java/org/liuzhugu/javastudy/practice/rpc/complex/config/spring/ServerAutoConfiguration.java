package org.liuzhugu.javastudy.practice.rpc.complex.config.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 注册中心自启配置
 * */
public class ServerAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    //spring会将已实例化的上下文注入  而不是加载XML  从新实例化
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
