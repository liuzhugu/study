package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.*;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy {
    private static final int PASSTHROUGH = 0;
    private static final int LOOKUP_OVERRIDE = 1;
    private static final int METHOD_REPLACER = 2;

    public CglibSubclassingInstantiationStrategy() {
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, String beanName, BeanFactory owner) {
        return this.instantiateWithMethodInjection(bd, beanName, owner, (Constructor)null);
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, String beanName, BeanFactory owner, Constructor<?> ctor, Object... args) {
        return (new CglibSubclassingInstantiationStrategy.CglibSubclassCreator(bd, owner)).instantiate(ctor, args);
    }

    private static class ReplaceOverrideMethodInterceptor extends CglibSubclassingInstantiationStrategy.CglibIdentitySupport implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor {
        private final BeanFactory owner;

        public ReplaceOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            ReplaceOverride ro = (ReplaceOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            MethodReplacer mr = (MethodReplacer)this.owner.getBean(ro.getMethodReplacerBeanName(), MethodReplacer.class);
            return mr.reimplement(obj, method, args);
        }
    }

    private static class LookupOverrideMethodInterceptor extends CglibSubclassingInstantiationStrategy.CglibIdentitySupport implements org.liuzhugu.javastudy.sourcecode.spring.aop.MethodInterceptor {
        private final BeanFactory owner;

        public LookupOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            LookupOverride lo = (LookupOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            Object[] argsToUse = args.length > 0 ? args : null;
            return StringUtils.hasText(lo.getBeanName()) ? this.owner.getBean(lo.getBeanName(), argsToUse) : this.owner.getBean(method.getReturnType(), argsToUse);
        }
    }

    private static class MethodOverrideCallbackFilter extends CglibSubclassingInstantiationStrategy.CglibIdentitySupport implements CallbackFilter {
        private static final Log logger = LogFactory.getLog(CglibSubclassingInstantiationStrategy.MethodOverrideCallbackFilter.class);

        public MethodOverrideCallbackFilter(RootBeanDefinition beanDefinition) {
            super(beanDefinition);
        }

        public int accept(Method method) {
            MethodOverride methodOverride = this.getBeanDefinition().getMethodOverrides().getOverride(method);
            if (logger.isTraceEnabled()) {
                logger.trace("Override for '" + method.getName() + "' is [" + methodOverride + "]");
            }

            if (methodOverride == null) {
                return 0;
            } else if (methodOverride instanceof LookupOverride) {
                return 1;
            } else if (methodOverride instanceof ReplaceOverride) {
                return 2;
            } else {
                throw new UnsupportedOperationException("Unexpected MethodOverride subclass: " + methodOverride.getClass().getName());
            }
        }
    }

    private static class ClassLoaderAwareGeneratorStrategy extends DefaultGeneratorStrategy {
        private final ClassLoader classLoader;

        public ClassLoaderAwareGeneratorStrategy(ClassLoader classLoader) {
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

    private static class CglibIdentitySupport {
        private final RootBeanDefinition beanDefinition;

        public CglibIdentitySupport(RootBeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        public RootBeanDefinition getBeanDefinition() {
            return this.beanDefinition;
        }

        public boolean equals(Object other) {
            return this.getClass() == other.getClass() && this.beanDefinition.equals(((CglibSubclassingInstantiationStrategy.CglibIdentitySupport)other).beanDefinition);
        }

        public int hashCode() {
            return this.beanDefinition.hashCode();
        }
    }

    private static class CglibSubclassCreator {
        private static final Class<?>[] CALLBACK_TYPES = new Class[]{NoOp.class, CglibSubclassingInstantiationStrategy.LookupOverrideMethodInterceptor.class, CglibSubclassingInstantiationStrategy.ReplaceOverrideMethodInterceptor.class};
        private final RootBeanDefinition beanDefinition;
        private final BeanFactory owner;

        CglibSubclassCreator(RootBeanDefinition beanDefinition, BeanFactory owner) {
            this.beanDefinition = beanDefinition;
            this.owner = owner;
        }

        public Object instantiate(Constructor<?> ctor, Object... args) {
            Class<?> subclass = this.createEnhancedSubclass(this.beanDefinition);
            Object instance;
            if (ctor == null) {
                instance = BeanUtils.instantiateClass(subclass);
            } else {
                try {
                    Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
                    instance = enhancedSubclassConstructor.newInstance(args);
                } catch (Exception var6) {
                    throw new BeanInstantiationException(this.beanDefinition.getBeanClass(), "Failed to invoke constructor for CGLIB enhanced subclass [" + subclass.getName() + "]", var6);
                }
            }

            Factory factory = (Factory)instance;
            factory.setCallbacks(new Callback[]{NoOp.INSTANCE, new CglibSubclassingInstantiationStrategy.LookupOverrideMethodInterceptor(this.beanDefinition, this.owner), new CglibSubclassingInstantiationStrategy.ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner)});
            return instance;
        }

        private Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            if (this.owner instanceof org.springframework.beans.factory.config.ConfigurableBeanFactory) {
                ClassLoader cl = ((ConfigurableBeanFactory)this.owner).getBeanClassLoader();
                enhancer.setStrategy(new CglibSubclassingInstantiationStrategy.ClassLoaderAwareGeneratorStrategy(cl));
            }

            enhancer.setCallbackFilter(new CglibSubclassingInstantiationStrategy.MethodOverrideCallbackFilter(beanDefinition));
            enhancer.setCallbackTypes(CALLBACK_TYPES);
            return enhancer.createClass();
        }
    }
}