package org.liuzhugu.javastudy.sourcecode.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ThrowsAdviceInterceptor implements MethodInterceptor, AfterAdvice {
    private static final String AFTER_THROWING = "afterThrowing";
    private static final Log logger = LogFactory.getLog(ThrowsAdviceInterceptor.class);
    private final Object throwsAdvice;
    private final Map<Class<?>, Method> exceptionHandlerMap = new HashMap();

    public ThrowsAdviceInterceptor(Object throwsAdvice) {
        Assert.notNull(throwsAdvice, "Advice must not be null");
        this.throwsAdvice = throwsAdvice;
        Method[] methods = throwsAdvice.getClass().getMethods();
        Method[] var3 = methods;
        int var4 = methods.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            if (method.getName().equals("afterThrowing") && (method.getParameterTypes().length == 1 || method.getParameterTypes().length == 4) && Throwable.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1])) {
                this.exceptionHandlerMap.put(method.getParameterTypes()[method.getParameterTypes().length - 1], method);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found exception handler method: " + method);
                }
            }
        }

        if (this.exceptionHandlerMap.isEmpty()) {
            throw new IllegalArgumentException("At least one handler method must be found in class [" + throwsAdvice.getClass() + "]");
        }
    }

    public int getHandlerMethodCount() {
        return this.exceptionHandlerMap.size();
    }

    private Method getExceptionHandler(Throwable exception) {
        Class<?> exceptionClass = exception.getClass();
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to find handler for exception of type [" + exceptionClass.getName() + "]");
        }

        Method handler;
        for(handler = (Method)this.exceptionHandlerMap.get(exceptionClass); handler == null && exceptionClass != Throwable.class; handler = (Method)this.exceptionHandlerMap.get(exceptionClass)) {
            exceptionClass = exceptionClass.getSuperclass();
        }

        if (handler != null && logger.isDebugEnabled()) {
            logger.debug("Found handler for exception of type [" + exceptionClass.getName() + "]: " + handler);
        }

        return handler;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable var4) {
            Method handlerMethod = this.getExceptionHandler(var4);
            if (handlerMethod != null) {
                this.invokeHandlerMethod(mi, var4, handlerMethod);
            }

            throw var4;
        }
    }

    private void invokeHandlerMethod(MethodInvocation mi, Throwable ex, Method method) throws Throwable {
        Object[] handlerArgs;
        if (method.getParameterTypes().length == 1) {
            handlerArgs = new Object[]{ex};
        } else {
            handlerArgs = new Object[]{mi.getMethod(), mi.getArguments(), mi.getThis(), ex};
        }

        try {
            method.invoke(this.throwsAdvice, handlerArgs);
        } catch (InvocationTargetException var6) {
            throw var6.getTargetException();
        }
    }
}