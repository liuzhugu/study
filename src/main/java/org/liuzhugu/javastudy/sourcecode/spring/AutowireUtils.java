package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

abstract class AutowireUtils {
    AutowireUtils() {
    }

    public static void sortConstructors(Constructor<?>[] constructors) {
        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
            public int compare(Constructor<?> c1, Constructor<?> c2) {
                boolean p1 = Modifier.isPublic(c1.getModifiers());
                boolean p2 = Modifier.isPublic(c2.getModifiers());
                if (p1 != p2) {
                    return p1 ? -1 : 1;
                } else {
                    int c1pl = c1.getParameterTypes().length;
                    int c2pl = c2.getParameterTypes().length;
                    return c1pl < c2pl ? 1 : (c1pl > c2pl ? -1 : 0);
                }
            }
        });
    }

    public static void sortFactoryMethods(Method[] factoryMethods) {
        Arrays.sort(factoryMethods, new Comparator<Method>() {
            public int compare(Method fm1, Method fm2) {
                boolean p1 = Modifier.isPublic(fm1.getModifiers());
                boolean p2 = Modifier.isPublic(fm2.getModifiers());
                if (p1 != p2) {
                    return p1 ? -1 : 1;
                } else {
                    int c1pl = fm1.getParameterTypes().length;
                    int c2pl = fm2.getParameterTypes().length;
                    return c1pl < c2pl ? 1 : (c1pl > c2pl ? -1 : 0);
                }
            }
        });
    }

    public static boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        Method wm = pd.getWriteMethod();
        if (wm == null) {
            return false;
        } else if (!wm.getDeclaringClass().getName().contains("$$")) {
            return false;
        } else {
            Class<?> superclass = wm.getDeclaringClass().getSuperclass();
            return !ClassUtils.hasMethod(superclass, wm.getName(), wm.getParameterTypes());
        }
    }

    public static boolean isSetterDefinedInInterface(PropertyDescriptor pd, Set<Class<?>> interfaces) {
        Method setter = pd.getWriteMethod();
        if (setter != null) {
            Class<?> targetClass = setter.getDeclaringClass();
            Iterator var4 = interfaces.iterator();

            while(var4.hasNext()) {
                Class<?> ifc = (Class)var4.next();
                if (ifc.isAssignableFrom(targetClass) && ClassUtils.hasMethod(ifc, setter.getName(), setter.getParameterTypes())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Object resolveAutowiringValue(Object autowiringValue, Class<?> requiredType) {
        if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
            ObjectFactory<?> factory = (ObjectFactory)autowiringValue;
            if (!(autowiringValue instanceof Serializable) || !requiredType.isInterface()) {
                return factory.getObject();
            }

            autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(), new Class[]{requiredType}, new AutowireUtils.ObjectFactoryDelegatingInvocationHandler(factory));
        }

        return autowiringValue;
    }

    public static Class<?> resolveReturnTypeForFactoryMethod(Method method, Object[] args, ClassLoader classLoader) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(args, "Argument array must not be null");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
        Type genericReturnType = method.getGenericReturnType();
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        Assert.isTrue(args.length == methodParameterTypes.length, "Argument array does not match parameter count");
        boolean locallyDeclaredTypeVariableMatchesReturnType = false;
        TypeVariable[] var7 = declaredTypeVariables;
        int var8 = declaredTypeVariables.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            TypeVariable<Method> currentTypeVariable = var7[var9];
            if (currentTypeVariable.equals(genericReturnType)) {
                locallyDeclaredTypeVariableMatchesReturnType = true;
                break;
            }
        }

        if (locallyDeclaredTypeVariableMatchesReturnType) {
            for(int i = 0; i < methodParameterTypes.length; ++i) {
                Type methodParameterType = methodParameterTypes[i];
                Object arg = args[i];
                if (methodParameterType.equals(genericReturnType)) {
                    if (arg instanceof TypedStringValue) {
                        TypedStringValue typedValue = (TypedStringValue)arg;
                        if (typedValue.hasTargetType()) {
                            return typedValue.getTargetType();
                        }

                        try {
                            return typedValue.resolveTargetType(classLoader);
                        } catch (ClassNotFoundException var19) {
                            throw new IllegalStateException("Failed to resolve value type [" + typedValue.getTargetTypeName() + "] for factory method argument", var19);
                        }
                    }

                    if (arg != null && !(arg instanceof BeanMetadataElement)) {
                        return arg.getClass();
                    }

                    return method.getReturnType();
                }

                if (methodParameterType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType)methodParameterType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Type[] var12 = actualTypeArguments;
                    int var13 = actualTypeArguments.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        Type typeArg = var12[var14];
                        if (typeArg.equals(genericReturnType)) {
                            if (arg instanceof Class) {
                                return (Class)arg;
                            }

                            String className = null;
                            if (arg instanceof String) {
                                className = (String)arg;
                            } else if (arg instanceof TypedStringValue) {
                                TypedStringValue typedValue = (TypedStringValue)arg;
                                String targetTypeName = typedValue.getTargetTypeName();
                                if (targetTypeName == null || Class.class.getName().equals(targetTypeName)) {
                                    className = typedValue.getValue();
                                }
                            }

                            if (className != null) {
                                try {
                                    return ClassUtils.forName(className, classLoader);
                                } catch (ClassNotFoundException var20) {
                                    throw new IllegalStateException("Could not resolve class name [" + arg + "] for factory method argument", var20);
                                }
                            }

                            return method.getReturnType();
                        }
                    }
                }
            }
        }

        return method.getReturnType();
    }

    private static class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler, Serializable {
        private final ObjectFactory<?> objectFactory;

        public ObjectFactoryDelegatingInvocationHandler(ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                return proxy == args[0];
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else if (methodName.equals("toString")) {
                return this.objectFactory.toString();
            } else {
                try {
                    return method.invoke(this.objectFactory.getObject(), args);
                } catch (InvocationTargetException var6) {
                    throw var6.getTargetException();
                }
            }
        }
    }
}