package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;

public interface AopProxyFactory {
    AopProxy createAopProxy(AdvisedSupport var1) throws AopConfigException;
}
