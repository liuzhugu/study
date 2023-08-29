package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedIntrospectionResults {
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag("spring.beaninfo.ignore");
    private static List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());
    private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);
    static final Set<ClassLoader> acceptedClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap(16));
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache = new ConcurrentHashMap(64);
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache = new ConcurrentReferenceHashMap(64);
    private final BeanInfo beanInfo;
    private final Map<String, PropertyDescriptor> propertyDescriptorCache;
    private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;

    public static void acceptClassLoader(ClassLoader classLoader) {
        if (classLoader != null) {
            acceptedClassLoaders.add(classLoader);
        }

    }

    public static void clearClassLoader(ClassLoader classLoader) {
        Iterator it = acceptedClassLoaders.iterator();

        while(it.hasNext()) {
            ClassLoader registeredLoader = (ClassLoader)it.next();
            if (isUnderneathClassLoader(registeredLoader, classLoader)) {
                it.remove();
            }
        }

        it = strongClassCache.keySet().iterator();

        Class beanClass;
        while(it.hasNext()) {
            beanClass = (Class)it.next();
            if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
                it.remove();
            }
        }

        it = softClassCache.keySet().iterator();

        while(it.hasNext()) {
            beanClass = (Class)it.next();
            if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
                it.remove();
            }
        }

    }

    static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        CachedIntrospectionResults results = (CachedIntrospectionResults)strongClassCache.get(beanClass);
        if (results != null) {
            return results;
        } else {
            results = (CachedIntrospectionResults)softClassCache.get(beanClass);
            if (results != null) {
                return results;
            } else {
                results = new CachedIntrospectionResults(beanClass);
                ConcurrentMap classCacheToUse;
                if (!ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) && !isClassLoaderAccepted(beanClass.getClassLoader())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
                    }

                    classCacheToUse = softClassCache;
                } else {
                    classCacheToUse = strongClassCache;
                }

                CachedIntrospectionResults existing = (CachedIntrospectionResults)classCacheToUse.putIfAbsent(beanClass, results);
                return existing != null ? existing : results;
            }
        }
    }

    private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
        Iterator var1 = acceptedClassLoaders.iterator();

        ClassLoader acceptedLoader;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            acceptedLoader = (ClassLoader)var1.next();
        } while(!isUnderneathClassLoader(classLoader, acceptedLoader));

        return true;
    }

    private static boolean isUnderneathClassLoader(ClassLoader candidate, ClassLoader parent) {
        if (candidate == parent) {
            return true;
        } else if (candidate == null) {
            return false;
        } else {
            ClassLoader classLoaderToCheck = candidate;

            do {
                if (classLoaderToCheck == null) {
                    return false;
                }

                classLoaderToCheck = classLoaderToCheck.getParent();
            } while(classLoaderToCheck != parent);

            return true;
        }
    }

    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }

            BeanInfo beanInfo = null;
            Iterator var3 = beanInfoFactories.iterator();

            while(var3.hasNext()) {
                BeanInfoFactory beanInfoFactory = (BeanInfoFactory)var3.next();
                beanInfo = beanInfoFactory.getBeanInfo(beanClass);
                if (beanInfo != null) {
                    break;
                }
            }

            if (beanInfo == null) {
                beanInfo = shouldIntrospectorIgnoreBeaninfoClasses ? Introspector.getBeanInfo(beanClass, 3) : Introspector.getBeanInfo(beanClass);
            }

            this.beanInfo = beanInfo;
            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }

            this.propertyDescriptorCache = new LinkedHashMap();
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            PropertyDescriptor[] var18 = pds;
            int var5 = pds.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                PropertyDescriptor pd = var18[var6];
                if (Class.class != beanClass || !"classLoader".equals(pd.getName()) && !"protectionDomain".equals(pd.getName())) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Found bean property '" + pd.getName() + "'" + (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") + (pd.getPropertyEditorClass() != null ? "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                    }

                    pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                    this.propertyDescriptorCache.put(pd.getName(), pd);
                }
            }

            for(Class clazz = beanClass; clazz != null; clazz = clazz.getSuperclass()) {
                Class<?>[] ifcs = clazz.getInterfaces();
                Class[] var21 = ifcs;
                int var22 = ifcs.length;

                for(int var8 = 0; var8 < var22; ++var8) {
                    Class<?> ifc = var21[var8];
                    BeanInfo ifcInfo = Introspector.getBeanInfo(ifc, 3);
                    PropertyDescriptor[] ifcPds = ifcInfo.getPropertyDescriptors();
                    PropertyDescriptor[] var12 = ifcPds;
                    int var13 = ifcPds.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        PropertyDescriptor pd = var12[var14];
                        if (!this.propertyDescriptorCache.containsKey(pd.getName())) {
                            pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                            this.propertyDescriptorCache.put(pd.getName(), pd);
                        }
                    }
                }
            }

            this.typeDescriptorCache = new ConcurrentReferenceHashMap();
        } catch (IntrospectionException var16) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", var16);
        }
    }

    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    PropertyDescriptor getPropertyDescriptor(String name) {
        PropertyDescriptor pd = (PropertyDescriptor)this.propertyDescriptorCache.get(name);
        if (pd == null && StringUtils.hasLength(name)) {
            pd = (PropertyDescriptor)this.propertyDescriptorCache.get(StringUtils.uncapitalize(name));
            if (pd == null) {
                pd = (PropertyDescriptor)this.propertyDescriptorCache.get(StringUtils.capitalize(name));
            }
        }

        return pd != null && !(pd instanceof GenericTypeAwarePropertyDescriptor) ? this.buildGenericTypeAwarePropertyDescriptor(this.getBeanClass(), pd) : pd;
    }

    PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
        int i = 0;

        for(Iterator var3 = this.propertyDescriptorCache.values().iterator(); var3.hasNext(); ++i) {
            PropertyDescriptor pd = (PropertyDescriptor)var3.next();
            pds[i] = pd instanceof GenericTypeAwarePropertyDescriptor ? pd : this.buildGenericTypeAwarePropertyDescriptor(this.getBeanClass(), pd);
        }

        return pds;
    }

    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), pd.getPropertyEditorClass());
        } catch (IntrospectionException var4) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", var4);
        }
    }

    TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
        TypeDescriptor existing = (TypeDescriptor)this.typeDescriptorCache.putIfAbsent(pd, td);
        return existing != null ? existing : td;
    }

    TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
        return (TypeDescriptor)this.typeDescriptorCache.get(pd);
    }
}