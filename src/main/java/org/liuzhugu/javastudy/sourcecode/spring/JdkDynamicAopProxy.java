package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DecoratingProxy;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
    private static final long serialVersionUID = 5531744639992436476L;
    private static final Log logger = LogFactory.getLog(JdkDynamicAopProxy.class);
    private final AdvisedSupport advised;
    private boolean equalsDefined;
    private boolean hashCodeDefined;

    public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        } else {
            this.advised = config;
        }
    }

    public Object getProxy() {
        return this.getProxy(ClassUtils.getDefaultClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {

        Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
        this.findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
        //使用Proxy 生成代理类
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }

    private void findDefinedEqualsAndHashCodeMethods(Class<?>[] proxiedInterfaces) {
        Class[] var2 = proxiedInterfaces;
        int var3 = proxiedInterfaces.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> proxiedInterface = var2[var4];
            Method[] methods = proxiedInterface.getDeclaredMethods();
            Method[] var7 = methods;
            int var8 = methods.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Method method = var7[var9];
                if (AopUtils.isEqualsMethod(method)) {
                    this.equalsDefined = true;
                }

                if (AopUtils.isHashCodeMethod(method)) {
                    this.hashCodeDefined = true;
                }

                if (this.equalsDefined && this.hashCodeDefined) {
                    return;
                }
            }
        }

    }

    /**
     * ￥ 增强方法
     * */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object oldProxy = null;
        boolean setProxyContext = false;
        TargetSource targetSource = this.advised.targetSource;
        Class<?> targetClass = null;
        Object target = null;

        Boolean var10;
        try {
            if (this.equalsDefined || !AopUtils.isEqualsMethod(method)) {
                if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                    Integer var20 = this.hashCode();
                    return var20;
                }

                if (method.getDeclaringClass() == DecoratingProxy.class) {
                    Class var18 = AopProxyUtils.ultimateTargetClass(this.advised);
                    return var18;
                }

                Object retVal;
                if (!this.advised.opaque && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                    retVal = AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
                    return retVal;
                }

                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }

                //获取AOP目标类
                target = targetSource.getTarget();
                if (target != null) {
                    targetClass = target.getClass();
                }

                //获取当前方法的拦截器链chain
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                //检查是否有advice  如果没有  需要回退到目标的直接反射调用 从而避免创建Method Invocation
                //如果chain为空  直接调用切点方法
                if (chain.isEmpty()) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
                } else {
                    //如果chain不为空  将拦截器封装在ReflectiveMethodInvocation  并执行
                    MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
                    //通过拦截器调用链执行join point
                    retVal = invocation.proceed();
                }

                Class<?> returnType = method.getReturnType();
                if (retVal != null && retVal == target && returnType != Object.class && returnType.isInstance(proxy) && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                    retVal = proxy;
                } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                    throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
                }

                Object var13 = retVal;
                return var13;
            }

            var10 = this.equals(args[0]);
        } finally {
            if (target != null && !targetSource.isStatic()) {
                targetSource.releaseTarget(target);
            }

            if (setProxyContext) {
                AopContext.setCurrentProxy(oldProxy);
            }

        }

        return var10;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else {
            JdkDynamicAopProxy otherProxy;
            if (other instanceof JdkDynamicAopProxy) {
                otherProxy = (JdkDynamicAopProxy)other;
            } else {
                if (!Proxy.isProxyClass(other.getClass())) {
                    return false;
                }

                InvocationHandler ih = Proxy.getInvocationHandler(other);
                if (!(ih instanceof JdkDynamicAopProxy)) {
                    return false;
                }

                otherProxy = (JdkDynamicAopProxy)ih;
            }

            return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
        }
    }

    public int hashCode() {
        return JdkDynamicAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }
}