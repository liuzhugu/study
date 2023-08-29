package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mustWatch 动态代理增强
 * */
public class ReflectiveMethodInvocation implements ProxyMethodInvocation, Cloneable {
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    private final Class<?> targetClass;
    private Map<String, Object> userAttributes;
    protected final List<?> interceptorsAndDynamicMethodMatchers;
    private int currentInterceptorIndex = -1;

    protected ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments);
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public final Object getProxy() {
        return this.proxy;
    }

    public final Object getThis() {
        return this.target;
    }

    public final AccessibleObject getStaticPart() {
        return this.method;
    }

    public final Method getMethod() {
        return this.method;
    }

    public final Object[] getArguments() {
        return this.arguments != null ? this.arguments : new Object[0];
    }

    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    //￥ AOP方法增强过程  拦截器组成调用链  每当发现匹配  那么匹配的拦截器去增强方法
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            //最后一环
            return this.invokeJoinpoint();
        } else {
            //没到最后  继续调用下一环
            Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
            if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
                InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher)interceptorOrInterceptionAdvice;
                return dm.methodMatcher.matches(this.method, this.targetClass, this.arguments) ?
                        //如果拦截器匹配  那么使用拦截器增强该方法
                        dm.interceptor.invoke(this) :
                        //匹配失败   跳过拦截器到拦截链的下一个拦截器
                        this.proceed();
            } else {
                //只是一个拦截器就通过静态的方式执行  不用执行嵌入其中的方法
                return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
            }
        }
    }

    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
    }

    public MethodInvocation invocableClone() {
        Object[] cloneArguments = null;
        if (this.arguments != null) {
            cloneArguments = new Object[this.arguments.length];
            System.arraycopy(this.arguments, 0, cloneArguments, 0, this.arguments.length);
        }

        return this.invocableClone(cloneArguments);
    }

    public MethodInvocation invocableClone(Object... arguments) {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap();
        }

        try {
            ReflectiveMethodInvocation clone = (ReflectiveMethodInvocation)this.clone();
            clone.arguments = arguments;
            return clone;
        } catch (CloneNotSupportedException var3) {
            throw new IllegalStateException("Should be able to clone object of type [" + this.getClass() + "]: " + var3);
        }
    }

    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap();
            }

            this.userAttributes.put(key, value);
        } else if (this.userAttributes != null) {
            this.userAttributes.remove(key);
        }

    }

    public Object getUserAttribute(String key) {
        return this.userAttributes != null ? this.userAttributes.get(key) : null;
    }

    public Map<String, Object> getUserAttributes() {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap();
        }

        return this.userAttributes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
        sb.append(this.method).append("; ");
        if (this.target == null) {
            sb.append("target is null");
        } else {
            sb.append("target is of class [").append(this.target.getClass().getName()).append(']');
        }

        return sb.toString();
    }
}