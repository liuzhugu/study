package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.*;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

class ConstructorResolver {
    private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint = new NamedThreadLocal("Current injection point");
    private final AbstractAutowireCapableBeanFactory beanFactory;

    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd, Constructor<?>[] chosenCtors, Object[] explicitArgs) {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        Constructor<?> constructorToUse = null;
        ConstructorResolver.ArgumentsHolder argsHolderToUse = null;
         Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized(mbd.constructorArgumentLock) {
                constructorToUse = (Constructor)mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }

            if (argsToResolve != null) {
                argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
            }
        }

        if (constructorToUse == null) {
            boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
            ConstructorArgumentValues resolvedValues = null;
            int minNrOfArgs;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }

            Constructor<?>[] candidates = chosenCtors;
            if (chosenCtors == null) {
                Class beanClass = mbd.getBeanClass();

                try {
                    candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
                } catch (Throwable var25) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", var25);
                }
            }

            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = 2147483647;
            Set<Constructor<?>> ambiguousConstructors = null;
            LinkedList<UnsatisfiedDependencyException> causes = null;
            Constructor[] var16 = candidates;
            int var17 = candidates.length;

            for(int var18 = 0; var18 < var17; ++var18) {
                Constructor<?> candidate = var16[var18];
                Class<?>[] paramTypes = candidate.getParameterTypes();
                if (constructorToUse != null && argsToUse.length > paramTypes.length) {
                    break;
                }

                if (paramTypes.length >= minNrOfArgs) {
                    ConstructorResolver.ArgumentsHolder argsHolder;
                    if (resolvedValues != null) {
                        try {
                            String[] paramNames = ConstructorResolver.ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
                            if (paramNames == null) {
                                ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                                if (pnd != null) {
                                    paramNames = pnd.getParameterNames(candidate);
                                }
                            }

                            argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, this.getUserDeclaredConstructor(candidate), autowiring);
                        } catch (UnsatisfiedDependencyException var26) {
                            if (this.beanFactory.logger.isTraceEnabled()) {
                                this.beanFactory.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + var26);
                            }

                            if (causes == null) {
                                causes = new LinkedList();
                            }

                            causes.add(var26);
                            continue;
                        }
                    } else {
                        if (paramTypes.length != explicitArgs.length) {
                            continue;
                        }

                        argsHolder = new ConstructorResolver.ArgumentsHolder(explicitArgs);
                    }

                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        constructorToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousConstructors = null;
                    } else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                        if (ambiguousConstructors == null) {
                            ambiguousConstructors = new LinkedHashSet();
                            ambiguousConstructors.add(constructorToUse);
                        }

                        ambiguousConstructors.add(candidate);
                    }
                }
            }

            if (constructorToUse == null) {
                if (causes == null) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
                }

                UnsatisfiedDependencyException ex = (UnsatisfiedDependencyException)causes.removeLast();
                Iterator var34 = causes.iterator();

                while(var34.hasNext()) {
                    Exception cause = (Exception)var34.next();
                    this.beanFactory.onSuppressedException(cause);
                }

                throw ex;
            }

            if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found in bean '" + beanName + "' (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
            }

            if (explicitArgs == null) {
                argsHolderToUse.storeCache(mbd, constructorToUse);
            }
        }

        try {
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                Constructor<?> finalConstructorToUse = constructorToUse;
                Object[] finalArgsToUse = argsToUse;
                beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        return ConstructorResolver.this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, ConstructorResolver.this.beanFactory, finalConstructorToUse, finalArgsToUse);
                    }
                }, this.beanFactory.getAccessControlContext());
            } else {
                beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
            }

            bw.setBeanInstance(beanInstance);
            return bw;
        } catch (Throwable var24) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via constructor failed", var24);
        }
    }

    public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
        Class factoryClass;
        boolean isStatic;
        if (mbd.getFactoryBeanName() != null) {
            factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
            isStatic = false;
        } else {
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }

        factoryClass = ClassUtils.getUserClass(factoryClass);
        Method[] candidates = this.getCandidateMethods(factoryClass, mbd);
        Method uniqueCandidate = null;
        Method[] var6 = candidates;
        int var7 = candidates.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Method candidate = var6[var8];
            if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                if (uniqueCandidate == null) {
                    uniqueCandidate = candidate;
                } else if (!Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes())) {
                    uniqueCandidate = null;
                    break;
                }
            }
        }

        synchronized(mbd.constructorArgumentLock) {
            mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
        }
    }

    private Method[] getCandidateMethods(final Class<?> factoryClass, final RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            return (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
                public Method[] run() {
                    return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
                }
            });
        } else {
            return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
        }
    }

    public BeanWrapper instantiateUsingFactoryMethod(final String beanName, final RootBeanDefinition mbd, Object[] explicitArgs) {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        String factoryBeanName = mbd.getFactoryBeanName();
        final Object factoryBean;
        Class factoryClass;
        boolean isStatic;
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }

            factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (factoryBean == null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "factory-bean '" + factoryBeanName + "' (or a BeanPostProcessor involved) returned null");
            }

            if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
                throw new IllegalStateException("About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to");
            }

            factoryClass = factoryBean.getClass();
            isStatic = false;
        } else {
            if (!mbd.hasBeanClass()) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
            }

            factoryBean = null;
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }

        Method factoryMethodToUse = null;
        ConstructorResolver.ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized(mbd.constructorArgumentLock) {
                factoryMethodToUse = (Method)mbd.resolvedConstructorOrFactoryMethod;
                if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }

            if (argsToResolve != null) {
                argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve);
            }
        }

        if (factoryMethodToUse == null || argsToUse == null) {
            factoryClass = ClassUtils.getUserClass(factoryClass);
            Method[] rawCandidates = this.getCandidateMethods(factoryClass, mbd);
            List<Method> candidateSet = new ArrayList();
            Method[] candidates = rawCandidates;
            int var15 = rawCandidates.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                Method candidate = candidates[var16];
                if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                    candidateSet.add(candidate);
                }
            }

            candidates = (Method[])candidateSet.toArray(new Method[candidateSet.size()]);
            //AutowireUtils.sortFactoryMethods(candidates);
            ConstructorArgumentValues resolvedValues = null;
            boolean autowiring = mbd.getResolvedAutowireMode() == 3;
            int minTypeDiffWeight = 2147483647;
            Set<Method> ambiguousFactoryMethods = null;
            int minNrOfArgs;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }

            LinkedList<UnsatisfiedDependencyException> causes = null;
            Method[] var21 = candidates;
            int var22 = candidates.length;

            int var23;
            for(var23 = 0; var23 < var22; ++var23) {
                Method candidate = var21[var23];
                Class<?>[] paramTypes = candidate.getParameterTypes();
                if (paramTypes.length >= minNrOfArgs) {
                    ConstructorResolver.ArgumentsHolder argsHolder;
                    if (resolvedValues != null) {
                        try {
                            String[] paramNames = null;
                            ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                            if (pnd != null) {
                                paramNames = pnd.getParameterNames(candidate);
                            }

                            argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, candidate, autowiring);
                        } catch (UnsatisfiedDependencyException var30) {
                            if (this.beanFactory.logger.isTraceEnabled()) {
                                this.beanFactory.logger.trace("Ignoring factory method [" + candidate + "] of bean '" + beanName + "': " + var30);
                            }

                            if (causes == null) {
                                causes = new LinkedList();
                            }

                            causes.add(var30);
                            continue;
                        }
                    } else {
                        if (paramTypes.length != explicitArgs.length) {
                            continue;
                        }

                        argsHolder = new ConstructorResolver.ArgumentsHolder(explicitArgs);
                    }

                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        factoryMethodToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousFactoryMethods = null;
                    } else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight && !mbd.isLenientConstructorResolution() && paramTypes.length == factoryMethodToUse.getParameterTypes().length && !Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
                        if (ambiguousFactoryMethods == null) {
                            ambiguousFactoryMethods = new LinkedHashSet();
                            ambiguousFactoryMethods.add(factoryMethodToUse);
                        }

                        ambiguousFactoryMethods.add(candidate);
                    }
                }
            }

            if (factoryMethodToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex = (UnsatisfiedDependencyException)causes.removeLast();
                    Iterator var43 = causes.iterator();

                    while(var43.hasNext()) {
                        Exception cause = (Exception)var43.next();
                        this.beanFactory.onSuppressedException(cause);
                    }

                    throw ex;
                }

                List<String> argTypes = new ArrayList(minNrOfArgs);
                if (explicitArgs != null) {
                    Object[] var40 = explicitArgs;
                    var23 = explicitArgs.length;

                    for(int var45 = 0; var45 < var23; ++var45) {
                        Object arg = var40[var45];
                        argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
                    }
                } else {
                    Set<ConstructorArgumentValues.ValueHolder> valueHolders = new LinkedHashSet(resolvedValues.getArgumentCount());
                    valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
                    valueHolders.addAll(resolvedValues.getGenericArgumentValues());
                    Iterator var44 = valueHolders.iterator();

                    while(var44.hasNext()) {
                        ConstructorArgumentValues.ValueHolder value = (ConstructorArgumentValues.ValueHolder)var44.next();
                        String argType = value.getType() != null ? ClassUtils.getShortName(value.getType()) : (value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null");
                        argTypes.add(argType);
                    }
                }

                String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found: " + (mbd.getFactoryBeanName() != null ? "factory bean '" + mbd.getFactoryBeanName() + "'; " : "") + "factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. Check that a method with the specified name " + (minNrOfArgs > 0 ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
            }

            if (Void.TYPE == factoryMethodToUse.getReturnType()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd.getFactoryMethodName() + "': needs to have a non-void return type!");
            }

            if (ambiguousFactoryMethods != null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found in bean '" + beanName + "' (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
            }

            if (explicitArgs == null && argsHolderToUse != null) {
                argsHolderToUse.storeCache(mbd, factoryMethodToUse);
            }
        }

        try {
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                Method finalFactoryMethodToUse = factoryMethodToUse;
                Object[] finalArgsToUse = argsToUse;
                beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        return ConstructorResolver.this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, ConstructorResolver.this.beanFactory, factoryBean, finalFactoryMethodToUse, finalArgsToUse);
                    }
                }, this.beanFactory.getAccessControlContext());
            } else {
                beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, factoryBean, factoryMethodToUse, argsToUse);
            }

            if (beanInstance == null) {
                return null;
            } else {
                bw.setBeanInstance(beanInstance);
                return bw;
            }
        } catch (Throwable var29) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via factory method failed", var29);
        }
    }

    private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, (TypeConverter)converter);
        int minNrOfArgs = cargs.getArgumentCount();
        Iterator var10 = cargs.getIndexedArgumentValues().entrySet().iterator();

        ConstructorArgumentValues.ValueHolder valueHolder;
        while(var10.hasNext()) {
            Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry = (Map.Entry)var10.next();
            int index = (Integer)entry.getKey();
            if (index < 0) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
            }

            if (index > minNrOfArgs) {
                minNrOfArgs = index + 1;
            }

            valueHolder = (ConstructorArgumentValues.ValueHolder)entry.getValue();
            if (valueHolder.isConverted()) {
                resolvedValues.addIndexedArgumentValue(index, valueHolder);
            } else {
                Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
                ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
                resolvedValueHolder.setSource(valueHolder);
                resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
            }
        }

        var10 = cargs.getGenericArgumentValues().iterator();

        while(var10.hasNext()) {
            valueHolder = (ConstructorArgumentValues.ValueHolder)var10.next();
            if (valueHolder.isConverted()) {
                resolvedValues.addGenericArgumentValue(valueHolder);
            } else {
                Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
                valueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
                valueHolder.setSource(valueHolder);
                resolvedValues.addGenericArgumentValue(valueHolder);
            }
        }

        return minNrOfArgs;
    }

    private ConstructorResolver.ArgumentsHolder createArgumentArray(String beanName, RootBeanDefinition mbd, ConstructorArgumentValues resolvedValues, BeanWrapper bw, Class<?>[] paramTypes, String[] paramNames, Object methodOrCtor, boolean autowiring) throws UnsatisfiedDependencyException {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        ConstructorResolver.ArgumentsHolder args = new ConstructorResolver.ArgumentsHolder(paramTypes.length);
        Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet(paramTypes.length);
        Set<String> autowiredBeanNames = new LinkedHashSet(4);

        for(int paramIndex = 0; paramIndex < paramTypes.length; ++paramIndex) {
            Class<?> paramType = paramTypes[paramIndex];
            String paramName = paramNames != null ? paramNames[paramIndex] : "";
            ConstructorArgumentValues.ValueHolder valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
            if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
                valueHolder = resolvedValues.getGenericArgumentValue((Class)null, (String)null, usedValueHolders);
            }

            Object convertedValue;
            if (valueHolder != null) {
                usedValueHolders.add(valueHolder);
                Object originalValue = valueHolder.getValue();
                if (valueHolder.isConverted()) {
                    convertedValue = valueHolder.getConvertedValue();
                    args.preparedArguments[paramIndex] = convertedValue;
                } else {
                    ConstructorArgumentValues.ValueHolder sourceHolder = (ConstructorArgumentValues.ValueHolder)valueHolder.getSource();
                    Object sourceValue = sourceHolder.getValue();
                    MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex);

                    try {
                        convertedValue = ((TypeConverter)converter).convertIfNecessary(originalValue, paramType, methodParam);
                        args.resolveNecessary = true;
                        args.preparedArguments[paramIndex] = sourceValue;
                    } catch (TypeMismatchException var25) {
                        throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType.getName() + "]: " + var25.getMessage());
                    }
                }

                args.arguments[paramIndex] = convertedValue;
                args.rawArguments[paramIndex] = originalValue;
            } else {
                MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex);
                if (!autowiring) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Ambiguous argument values for parameter of type [" + paramType.getName() + "] - did you specify the correct bean references as arguments?");
                }

                try {
                    convertedValue = this.resolveAutowiredArgument(methodParam, beanName, autowiredBeanNames, (TypeConverter)converter);
                    args.rawArguments[paramIndex] = convertedValue;
                    args.arguments[paramIndex] = convertedValue;
                    args.preparedArguments[paramIndex] = new ConstructorResolver.AutowiredArgumentMarker();
                    args.resolveNecessary = true;
                } catch (BeansException var24) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), var24);
                }
            }
        }

        Iterator var26 = autowiredBeanNames.iterator();

        while(var26.hasNext()) {
            String autowiredBeanName = (String)var26.next();
            this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
            if (this.beanFactory.logger.isDebugEnabled()) {
                this.beanFactory.logger.debug("Autowiring by type from bean name '" + beanName + "' via " + (methodOrCtor instanceof Constructor ? "constructor" : "factory method") + " to bean named '" + autowiredBeanName + "'");
            }
        }

        return args;
    }

    private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, Member methodOrCtor, Object[] argsToResolve) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, (TypeConverter)converter);
        Class<?>[] paramTypes = methodOrCtor instanceof Method ? ((Method)methodOrCtor).getParameterTypes() : ((Constructor)methodOrCtor).getParameterTypes();
        Object[] resolvedArgs = new Object[argsToResolve.length];

        for(int argIndex = 0; argIndex < argsToResolve.length; ++argIndex) {
            Object argValue = argsToResolve[argIndex];
            MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, argIndex);
            GenericTypeResolver.resolveParameterType(methodParam, methodOrCtor.getDeclaringClass());
            if (argValue instanceof ConstructorResolver.AutowiredArgumentMarker) {
                argValue = this.resolveAutowiredArgument(methodParam, beanName, (Set)null, (TypeConverter)converter);
            } else if (argValue instanceof BeanMetadataElement) {
                argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
            } else if (argValue instanceof String) {
                argValue = this.beanFactory.evaluateBeanDefinitionString((String)argValue, mbd);
            }

            Class paramType = paramTypes[argIndex];

            try {
                resolvedArgs[argIndex] = ((TypeConverter)converter).convertIfNecessary(argValue, paramType, methodParam);
            } catch (TypeMismatchException var16) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType.getName() + "]: " + var16.getMessage());
            }
        }

        return resolvedArgs;
    }

    protected Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        Class<?> userClass = ClassUtils.getUserClass(declaringClass);
        if (userClass != declaringClass) {
            try {
                return userClass.getDeclaredConstructor(constructor.getParameterTypes());
            } catch (NoSuchMethodException var5) {
            }
        }

        return constructor;
    }

    protected Object resolveAutowiredArgument(MethodParameter param, String beanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) {
        if (InjectionPoint.class.isAssignableFrom(param.getParameterType())) {
            InjectionPoint injectionPoint = (InjectionPoint)currentInjectionPoint.get();
            if (injectionPoint == null) {
                throw new IllegalStateException("No current InjectionPoint available for " + param);
            } else {
                return injectionPoint;
            }
        } else {
            return this.beanFactory.resolveDependency(new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
        }
    }

    static InjectionPoint setCurrentInjectionPoint(InjectionPoint injectionPoint) {
        InjectionPoint old = (InjectionPoint)currentInjectionPoint.get();
        if (injectionPoint != null) {
            currentInjectionPoint.set(injectionPoint);
        } else {
            currentInjectionPoint.remove();
        }

        return old;
    }

    private static class ConstructorPropertiesChecker {
        private ConstructorPropertiesChecker() {
        }

        public static String[] evaluate(Constructor<?> candidate, int paramCount) {
            ConstructorProperties cp = (ConstructorProperties)candidate.getAnnotation(ConstructorProperties.class);
            if (cp != null) {
                String[] names = cp.value();
                if (names.length != paramCount) {
                    throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
                } else {
                    return names;
                }
            } else {
                return null;
            }
        }
    }

    private static class AutowiredArgumentMarker {
        private AutowiredArgumentMarker() {
        }
    }

    private static class ArgumentsHolder {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }

        public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
            int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return rawTypeDiffWeight < typeDiffWeight ? rawTypeDiffWeight : typeDiffWeight;
        }

        public int getAssignabilityWeight(Class<?>[] paramTypes) {
            int i;
            for(i = 0; i < paramTypes.length; ++i) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
                    return 2147483647;
                }
            }

            for(i = 0; i < paramTypes.length; ++i) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
                    return 2147483135;
                }
            }

            return 2147482623;
        }

        public void storeCache(RootBeanDefinition mbd, Object constructorOrFactoryMethod) {
            synchronized(mbd.constructorArgumentLock) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                } else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }

            }
        }
    }
}
