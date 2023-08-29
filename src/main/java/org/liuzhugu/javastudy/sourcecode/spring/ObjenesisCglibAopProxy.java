package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.objenesis.SpringObjenesis;

class ObjenesisCglibAopProxy extends CglibAopProxy {
    private static final Log logger = LogFactory.getLog(ObjenesisCglibAopProxy.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();

    public ObjenesisCglibAopProxy(AdvisedSupport config) {
        super(config);
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        Class<?> proxyClass = enhancer.createClass();
        Object proxyInstance = null;
        if (objenesis.isWorthTrying()) {
            try {
                proxyInstance = objenesis.newInstance(proxyClass, enhancer.getUseCache());
            } catch (Throwable var7) {
                logger.debug("Unable to instantiate proxy using Objenesis, falling back to regular proxy construction", var7);
            }
        }

        if (proxyInstance == null) {
            try {
                proxyInstance = this.constructorArgs != null ? proxyClass.getConstructor(this.constructorArgTypes).newInstance(this.constructorArgs) : proxyClass.newInstance();
            } catch (Throwable var6) {
                throw new AopConfigException("Unable to instantiate proxy using Objenesis, and regular proxy instantiation via default constructor fails as well", var6);
            }
        }

        ((Factory)proxyInstance).setCallbacks(callbacks);
        return proxyInstance;
    }
}
