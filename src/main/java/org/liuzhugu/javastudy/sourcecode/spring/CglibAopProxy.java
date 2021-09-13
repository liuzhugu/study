package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.*;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import org.springframework.core.SmartClassLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;

class CglibAopProxy implements AopProxy, Serializable {
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;
    protected static final Log logger = LogFactory.getLog(CglibAopProxy.class);
    private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap();
    protected final AdvisedSupport advised;
    protected Object[] constructorArgs;
    protected Class<?>[] constructorArgTypes;
    private final transient CglibAopProxy.AdvisedDispatcher advisedDispatcher;
    private transient Map<String, Integer> fixedInterceptorMap;
    private transient int fixedInterceptorOffset;

    public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        } else {
            this.advised = config;
            this.advisedDispatcher = new CglibAopProxy.AdvisedDispatcher(this.advised);
        }
    }

    public void setConstructorArguments(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
        if (constructorArgs != null && constructorArgTypes != null) {
            if (constructorArgs.length != constructorArgTypes.length) {
                throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length + ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
            } else {
                this.constructorArgs = constructorArgs;
                this.constructorArgTypes = constructorArgTypes;
            }
        } else {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
    }

    public Object getProxy() {
        return this.getProxy((ClassLoader)null);
    }

    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
        }

        try {
            Class<?> rootClass = this.advised.getTargetClass();
            Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");
            Class<?> proxySuperClass = rootClass;
            int x;
            if (ClassUtils.isCglibProxyClass(rootClass)) {
                proxySuperClass = rootClass.getSuperclass();
                Class<?>[] additionalInterfaces = rootClass.getInterfaces();
                Class[] var5 = additionalInterfaces;
                int var6 = additionalInterfaces.length;

                for(x = 0; x < var6; ++x) {
                    Class<?> additionalInterface = var5[x];
                    this.advised.addInterface(additionalInterface);
                }
            }

            this.validateClassIfNecessary(proxySuperClass, classLoader);
            Enhancer enhancer = this.createEnhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
                if (classLoader instanceof SmartClassLoader && ((SmartClassLoader)classLoader).isClassReloadable(proxySuperClass)) {
                    enhancer.setUseCache(false);
                }
            }

            enhancer.setSuperclass(proxySuperClass);
            enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setStrategy(new CglibAopProxy.ClassLoaderAwareUndeclaredThrowableStrategy(classLoader));
            Callback[] callbacks = this.getCallbacks(rootClass);
            Class<?>[] types = new Class[callbacks.length];

            for(x = 0; x < types.length; ++x) {
                types[x] = callbacks[x].getClass();
            }

            enhancer.setCallbackFilter(new CglibAopProxy.ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
            enhancer.setCallbackTypes(types);
            return this.createProxyClassAndInstance(enhancer, callbacks);
        } catch (CodeGenerationException var9) {
            throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() + ": Common causes of this problem include using a final class or a non-visible class", var9);
        } catch (IllegalArgumentException var10) {
            throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() + ": Common causes of this problem include using a final class or a non-visible class", var10);
        } catch (Throwable var11) {
            throw new AopConfigException("Unexpected AOP exception", var11);
        }
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        return this.constructorArgs != null ? enhancer.create(this.constructorArgTypes, this.constructorArgs) : enhancer.create();
    }

    protected Enhancer createEnhancer() {
        return new Enhancer();
    }

    private void validateClassIfNecessary(Class<?> proxySuperClass, ClassLoader proxyClassLoader) {
        if (logger.isWarnEnabled()) {
            synchronized(validatedClasses) {
                if (!validatedClasses.containsKey(proxySuperClass)) {
                    this.doValidateClass(proxySuperClass, proxyClassLoader, ClassUtils.getAllInterfacesForClassAsSet(proxySuperClass));
                    validatedClasses.put(proxySuperClass, Boolean.TRUE);
                }
            }
        }

    }

    private void doValidateClass(Class<?> proxySuperClass, ClassLoader proxyClassLoader, Set<Class<?>> ifcs) {
        if (proxySuperClass != Object.class) {
            Method[] methods = proxySuperClass.getDeclaredMethods();
            Method[] var5 = methods;
            int var6 = methods.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Method method = var5[var7];
                int mod = method.getModifiers();
                if (!Modifier.isStatic(mod) && !Modifier.isPrivate(mod)) {
                    if (Modifier.isFinal(mod)) {
                        if (implementsInterface(method, ifcs)) {
                            logger.warn("Unable to proxy interface-implementing method [" + method + "] because it is marked as final: Consider using interface-based JDK proxies instead!");
                        }

                        logger.info("Final method [" + method + "] cannot get proxied via CGLIB: Calls to this method will NOT be routed to the target instance and might lead to NPEs against uninitialized fields in the proxy instance.");
                    } else if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod) && proxyClassLoader != null && proxySuperClass.getClassLoader() != proxyClassLoader) {
                        logger.info("Method [" + method + "] is package-visible across different ClassLoaders and cannot get proxied via CGLIB: Declare this method as public or protected if you need to support invocations through the proxy.");
                    }
                }
            }

            this.doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader, ifcs);
        }

    }

    private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
        boolean exposeProxy = this.advised.isExposeProxy();
        boolean isFrozen = this.advised.isFrozen();
        boolean isStatic = this.advised.getTargetSource().isStatic();
        Callback aopInterceptor = new CglibAopProxy.DynamicAdvisedInterceptor(this.advised);
        Object targetInterceptor;
        if (exposeProxy) {
            targetInterceptor = isStatic ? new CglibAopProxy.StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) : new CglibAopProxy.DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
        } else {
            targetInterceptor = isStatic ? new CglibAopProxy.StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) : new CglibAopProxy.DynamicUnadvisedInterceptor(this.advised.getTargetSource());
        }

        Callback targetDispatcher = (Callback)(isStatic ? new CglibAopProxy.StaticDispatcher(this.advised.getTargetSource().getTarget()) : new CglibAopProxy.SerializableNoOp());
        Callback[] mainCallbacks = new Callback[]{aopInterceptor, (Callback)targetInterceptor, new CglibAopProxy.SerializableNoOp(), targetDispatcher, this.advisedDispatcher, new CglibAopProxy.EqualsInterceptor(this.advised), new CglibAopProxy.HashCodeInterceptor(this.advised)};
        Callback[] callbacks;
        if (isStatic && isFrozen) {
            Method[] methods = rootClass.getMethods();
            Callback[] fixedCallbacks = new Callback[methods.length];
            this.fixedInterceptorMap = new HashMap(methods.length);

            for(int x = 0; x < methods.length; ++x) {
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
                fixedCallbacks[x] = new CglibAopProxy.FixedChainStaticTargetInterceptor(chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
                this.fixedInterceptorMap.put(methods[x].toString(), x);
            }

            callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
            System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
            System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
            this.fixedInterceptorOffset = mainCallbacks.length;
        } else {
            callbacks = mainCallbacks;
        }

        return callbacks;
    }

    public boolean equals(Object other) {
        return this == other || other instanceof CglibAopProxy && AopProxyUtils.equalsInProxy(this.advised, ((CglibAopProxy)other).advised);
    }

    public int hashCode() {
        return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }

    private static boolean implementsInterface(Method method, Set<Class<?>> ifcs) {
        Iterator var2 = ifcs.iterator();

        Class ifc;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            ifc = (Class)var2.next();
        } while(!ClassUtils.hasMethod(ifc, method.getName(), method.getParameterTypes()));

        return true;
    }

    private static Object processReturnType(Object proxy, Object target, Method method, Object retVal) {
        if (retVal != null && retVal == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            retVal = proxy;
        }

        Class<?> returnType = method.getReturnType();
        if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
        } else {
            return retVal;
        }
    }

    private static class ClassLoaderAwareUndeclaredThrowableStrategy extends UndeclaredThrowableStrategy {
        private final ClassLoader classLoader;

        public ClassLoaderAwareUndeclaredThrowableStrategy(ClassLoader classLoader) {
            super(UndeclaredThrowableException.class);
            this.classLoader = classLoader;
        }

        public byte[] generate(ClassGenerator cg) throws Exception {
            if (this.classLoader == null) {
                return super.generate(cg);
            } else {
                Thread currentThread = Thread.currentThread();

                ClassLoader threadContextClassLoader;
                try {
                    threadContextClassLoader = currentThread.getContextClassLoader();
                } catch (Throwable var9) {
                    return super.generate(cg);
                }

                boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
                if (overrideClassLoader) {
                    currentThread.setContextClassLoader(this.classLoader);
                }

                byte[] var5;
                try {
                    var5 = super.generate(cg);
                } finally {
                    if (overrideClassLoader) {
                        currentThread.setContextClassLoader(threadContextClassLoader);
                    }

                }

                return var5;
            }
        }
    }

    private static class ProxyCallbackFilter implements CallbackFilter {
        private final AdvisedSupport advised;
        private final Map<String, Integer> fixedInterceptorMap;
        private final int fixedInterceptorOffset;

        public ProxyCallbackFilter(AdvisedSupport advised, Map<String, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }

        public int accept(Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                CglibAopProxy.logger.debug("Found finalize() method - using NO_OVERRIDE");
                return 2;
            } else if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (CglibAopProxy.logger.isDebugEnabled()) {
                    CglibAopProxy.logger.debug("Method is declared on Advised interface: " + method);
                }

                return 4;
            } else if (AopUtils.isEqualsMethod(method)) {
                CglibAopProxy.logger.debug("Found 'equals' method: " + method);
                return 5;
            } else if (AopUtils.isHashCodeMethod(method)) {
                CglibAopProxy.logger.debug("Found 'hashCode' method: " + method);
                return 6;
            } else {
                Class<?> targetClass = this.advised.getTargetClass();
                List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                boolean haveAdvice = !chain.isEmpty();
                boolean exposeProxy = this.advised.isExposeProxy();
                boolean isStatic = this.advised.getTargetSource().isStatic();
                boolean isFrozen = this.advised.isFrozen();
                if (!haveAdvice && isFrozen) {
                    if (!exposeProxy && isStatic) {
                        Class<?> returnType = method.getReturnType();
                        if (returnType.isAssignableFrom(targetClass)) {
                            if (CglibAopProxy.logger.isDebugEnabled()) {
                                CglibAopProxy.logger.debug("Method return type is assignable from target type and may therefore return 'this' - using INVOKE_TARGET: " + method);
                            }

                            return 1;
                        } else {
                            if (CglibAopProxy.logger.isDebugEnabled()) {
                                CglibAopProxy.logger.debug("Method return type ensures 'this' cannot be returned - using DISPATCH_TARGET: " + method);
                            }

                            return 3;
                        }
                    } else {
                        return 1;
                    }
                } else if (exposeProxy) {
                    if (CglibAopProxy.logger.isDebugEnabled()) {
                        CglibAopProxy.logger.debug("Must expose proxy on advised method: " + method);
                    }

                    return 0;
                } else {
                    String key = method.toString();
                    if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
                        if (CglibAopProxy.logger.isDebugEnabled()) {
                            CglibAopProxy.logger.debug("Method has advice and optimizations are enabled: " + method);
                        }

                        int index = (Integer)this.fixedInterceptorMap.get(key);
                        return index + this.fixedInterceptorOffset;
                    } else {
                        if (CglibAopProxy.logger.isDebugEnabled()) {
                            CglibAopProxy.logger.debug("Unable to apply any optimizations to advised method: " + method);
                        }

                        return 0;
                    }
                }
            }
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof CglibAopProxy.ProxyCallbackFilter)) {
                return false;
            } else {
                CglibAopProxy.ProxyCallbackFilter otherCallbackFilter = (CglibAopProxy.ProxyCallbackFilter)other;
                AdvisedSupport otherAdvised = otherCallbackFilter.advised;
                if (this.advised != null && otherAdvised != null) {
                    if (this.advised.isFrozen() != otherAdvised.isFrozen()) {
                        return false;
                    } else if (this.advised.isExposeProxy() != otherAdvised.isExposeProxy()) {
                        return false;
                    } else if (this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
                        return false;
                    } else if (!AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
                        return false;
                    } else {
                        Advisor[] thisAdvisors = this.advised.getAdvisors();
                        Advisor[] thatAdvisors = otherAdvised.getAdvisors();
                        if (thisAdvisors.length != thatAdvisors.length) {
                            return false;
                        } else {
                            for(int i = 0; i < thisAdvisors.length; ++i) {
                                Advisor thisAdvisor = thisAdvisors[i];
                                Advisor thatAdvisor = thatAdvisors[i];
                                if (!this.equalsAdviceClasses(thisAdvisor, thatAdvisor)) {
                                    return false;
                                }

                                if (!this.equalsPointcuts(thisAdvisor, thatAdvisor)) {
                                    return false;
                                }
                            }

                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        private boolean equalsAdviceClasses(Advisor a, Advisor b) {
            Advice aa = a.getAdvice();
            Advice ba = b.getAdvice();
            if (aa != null && ba != null) {
                return aa.getClass() == ba.getClass();
            } else {
                return aa == ba;
            }
        }

        private boolean equalsPointcuts(Advisor a, Advisor b) {
            return !(a instanceof org.springframework.aop.PointcutAdvisor) || b instanceof org.springframework.aop.PointcutAdvisor && ObjectUtils.nullSafeEquals(((org.springframework.aop.PointcutAdvisor)a).getPointcut(), ((PointcutAdvisor)b).getPointcut());
        }

        public int hashCode() {
            int hashCode = 0;
            Advisor[] advisors = this.advised.getAdvisors();
            Advisor[] var3 = advisors;
            int var4 = advisors.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Advisor advisor = var3[var5];
                Advice advice = advisor.getAdvice();
                if (advice != null) {
                    hashCode = 13 * hashCode + advice.getClass().hashCode();
                }
            }

            hashCode = 13 * hashCode + (this.advised.isFrozen() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isExposeProxy() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOptimize() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOpaque() ? 1 : 0);
            return hashCode;
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
        private final MethodProxy methodProxy;
        private final boolean publicMethod;

        public CglibMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
            super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
            this.methodProxy = methodProxy;
            this.publicMethod = Modifier.isPublic(method.getModifiers());
        }

        protected Object invokeJoinpoint() throws Throwable {
            return this.publicMethod ? this.methodProxy.invoke(this.target, this.arguments) : super.invokeJoinpoint();
        }
    }

    private static class DynamicAdvisedInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            boolean setProxyContext = false;
            Class<?> targetClass = null;
            Object target = null;

            Object var15;
            try {
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }

                target = this.getTarget();
                if (target != null) {
                    targetClass = target.getClass();
                }

                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                Object retVal;
                if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = methodProxy.invoke(target, argsToUse);
                } else {
                    retVal = (new CglibAopProxy.CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy)).proceed();
                }

                retVal = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                var15 = retVal;
            } finally {
                if (target != null) {
                    this.releaseTarget(target);
                }

                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }

            }

            return var15;
        }

        public boolean equals(Object other) {
            return this == other || other instanceof CglibAopProxy.DynamicAdvisedInterceptor && this.advised.equals(((CglibAopProxy.DynamicAdvisedInterceptor)other).advised);
        }

        public int hashCode() {
            return this.advised.hashCode();
        }

        protected Object getTarget() throws Exception {
            return this.advised.getTargetSource().getTarget();
        }

        protected void releaseTarget(Object target) throws Exception {
            this.advised.getTargetSource().releaseTarget(target);
        }
    }

    private static class FixedChainStaticTargetInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final List<Object> adviceChain;
        private final Object target;
        private final Class<?> targetClass;

        public FixedChainStaticTargetInterceptor(List<Object> adviceChain, Object target, Class<?> targetClass) {
            this.adviceChain = adviceChain;
            this.target = target;
            this.targetClass = targetClass;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            MethodInvocation invocation = new CglibAopProxy.CglibMethodInvocation(proxy, this.target, method, args, this.targetClass, this.adviceChain, methodProxy);
            Object retVal = invocation.proceed();
            retVal = CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
            return retVal;
        }
    }

    private static class HashCodeInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public HashCodeInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
        }
    }

    private static class EqualsInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public EqualsInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            Object other = args[0];
            if (proxy == other) {
                return true;
            } else if (other instanceof Factory) {
                Callback callback = ((Factory)other).getCallback(5);
                if (!(callback instanceof CglibAopProxy.EqualsInterceptor)) {
                    return false;
                } else {
                    AdvisedSupport otherAdvised = ((CglibAopProxy.EqualsInterceptor)callback).advised;
                    return AopProxyUtils.equalsInProxy(this.advised, otherAdvised);
                }
            } else {
                return false;
            }
        }
    }

    private static class AdvisedDispatcher implements Dispatcher, Serializable {
        private final AdvisedSupport advised;

        public AdvisedDispatcher(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object loadObject() throws Exception {
            return this.advised;
        }
    }

    private static class StaticDispatcher implements Dispatcher, Serializable {
        private Object target;

        public StaticDispatcher(Object target) {
            this.target = target;
        }

        public Object loadObject() {
            return this.target;
        }
    }

    private static class DynamicUnadvisedExposedInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedExposedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            Object target = this.targetSource.getTarget();

            Object var8;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = methodProxy.invoke(target, args);
                var8 = CglibAopProxy.processReturnType(proxy, target, method, retVal);
            } finally {
                AopContext.setCurrentProxy(oldProxy);
                this.targetSource.releaseTarget(target);
            }

            return var8;
        }
    }

    private static class DynamicUnadvisedInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.targetSource.getTarget();

            Object var7;
            try {
                Object retVal = methodProxy.invoke(target, args);
                var7 = CglibAopProxy.processReturnType(proxy, target, method, retVal);
            } finally {
                this.targetSource.releaseTarget(target);
            }

            return var7;
        }
    }

    private static class StaticUnadvisedExposedInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final Object target;

        public StaticUnadvisedExposedInterceptor(Object target) {
            this.target = target;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;

            Object var7;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = methodProxy.invoke(this.target, args);
                var7 = CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
            } finally {
                AopContext.setCurrentProxy(oldProxy);
            }

            return var7;
        }
    }

    private static class StaticUnadvisedInterceptor implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor, Serializable {
        private final Object target;

        public StaticUnadvisedInterceptor(Object target) {
            this.target = target;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal = methodProxy.invoke(this.target, args);
            return CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
        }
    }

    public static class SerializableNoOp implements NoOp, Serializable {
        public SerializableNoOp() {
        }
    }
}