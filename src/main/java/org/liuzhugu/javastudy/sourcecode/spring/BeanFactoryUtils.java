package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

public abstract class BeanFactoryUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    public BeanFactoryUtils() {
    }

    public static boolean isFactoryDereference(String name) {
        return name != null && name.startsWith("&");
    }

    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");

        String beanName;
        for(beanName = name; beanName.startsWith("&"); beanName = beanName.substring("&".length())) {
        }

        return beanName;
    }

    public static boolean isGeneratedBeanName(String name) {
        return name != null && name.contains("#");
    }

    public static String originalBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        int separatorIndex = name.indexOf("#");
        return separatorIndex != -1 ? name.substring(0, separatorIndex) : name;
    }

    public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesIncludingAncestors(lbf).length;
    }

    public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesForTypeIncludingAncestors(lbf, Object.class);
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof org.springframework.beans.factory.HierarchicalBeanFactory) {
            org.springframework.beans.factory.HierarchicalBeanFactory hbf = (org.springframework.beans.factory.HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }

        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof org.springframework.beans.factory.HierarchicalBeanFactory) {
            org.springframework.beans.factory.HierarchicalBeanFactory hbf = (org.springframework.beans.factory.HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }

        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof org.springframework.beans.factory.HierarchicalBeanFactory) {
            org.springframework.beans.factory.HierarchicalBeanFactory hbf = (org.springframework.beans.factory.HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }

        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type));
        if (lbf instanceof org.springframework.beans.factory.HierarchicalBeanFactory) {
            org.springframework.beans.factory.HierarchicalBeanFactory hbf = (org.springframework.beans.factory.HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
                Iterator var5 = parentResult.entrySet().iterator();

                while(var5.hasNext()) {
                    Map.Entry<String, T> entry = (Map.Entry)var5.next();
                    String beanName = (String)entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }

        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
        if (lbf instanceof org.springframework.beans.factory.HierarchicalBeanFactory) {
            org.springframework.beans.factory.HierarchicalBeanFactory hbf = (org.springframework.beans.factory.HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                Iterator var7 = parentResult.entrySet().iterator();

                while(var7.hasNext()) {
                    Map.Entry<String, T> entry = (Map.Entry)var7.next();
                    String beanName = (String)entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }

        return result;
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
        return uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
        return uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type);
        return uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
        return uniqueBean(type, beansOfType);
    }

    private static String[] mergeNamesWithParent(String[] result, String[] parentResult, HierarchicalBeanFactory hbf) {
        if (parentResult.length == 0) {
            return result;
        } else {
            List<String> merged = new ArrayList(result.length + parentResult.length);
            merged.addAll(Arrays.asList(result));
            String[] var4 = parentResult;
            int var5 = parentResult.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String beanName = var4[var6];
                if (!merged.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                    merged.add(beanName);
                }
            }

            return StringUtils.toStringArray(merged);
        }
    }

    private static <T> T uniqueBean(Class<T> type, Map<String, T> matchingBeans) {
        int count = matchingBeans.size();
        if (count == 1) {
            return matchingBeans.values().iterator().next();
        } else if (count > 1) {
            throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
        } else {
            throw new NoSuchBeanDefinitionException(type);
        }
    }
}
