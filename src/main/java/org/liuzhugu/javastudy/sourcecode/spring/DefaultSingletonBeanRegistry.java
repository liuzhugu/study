package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    protected static final Object NULL_OBJECT = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    private final Map<String, Object> earlySingletonObjects = new HashMap(16);
    private final Set<String> registeredSingletons = new LinkedHashSet(256);
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private Set<Exception> suppressedExceptions;
    private boolean singletonsCurrentlyInDestruction = false;
    private final Map<String, Object> disposableBeans = new LinkedHashMap();
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap(16);
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap(64);
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap(64);

    public DefaultSingletonBeanRegistry() {
    }

    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized(this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            } else {
                this.addSingleton(beanName, singletonObject);
            }
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized(this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject != null ? singletonObject : NULL_OBJECT);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized(this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }

        }
    }

    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, true);
    }

    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
            synchronized(this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = (ObjectFactory)this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }

        return singletonObject != NULL_OBJECT ? singletonObject : null;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized(this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }

                this.beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                boolean recordSuppressedExceptions = this.suppressedExceptions == null;
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet();
                }

                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (IllegalStateException var16) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw var16;
                    }
                } catch (BeanCreationException var17) {
                    BeanCreationException ex = var17;
                    if (recordSuppressedExceptions) {
                        Iterator var8 = this.suppressedExceptions.iterator();

                        while(var8.hasNext()) {
                            Exception suppressedException = (Exception)var8.next();
                            ex.addRelatedCause(suppressedException);
                        }
                    }

                    throw ex;
                } finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }

                    this.afterSingletonCreation(beanName);
                }

                if (newSingleton) {
                    this.addSingleton(beanName, singletonObject);
                }
            }

            return singletonObject != NULL_OBJECT ? singletonObject : null;
        }
    }

    protected void onSuppressedException(Exception ex) {
        synchronized(this.singletonObjects) {
            if (this.suppressedExceptions != null) {
                this.suppressedExceptions.add(ex);
            }

        }
    }

    protected void removeSingleton(String beanName) {
        synchronized(this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    public String[] getSingletonNames() {
        synchronized(this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }

    public int getSingletonCount() {
        synchronized(this.singletonObjects) {
            return this.registeredSingletons.size();
        }
    }

    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        Assert.notNull(beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.add(beanName);
        } else {
            this.inCreationCheckExclusions.remove(beanName);
        }

    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return !this.inCreationCheckExclusions.contains(beanName) && this.isActuallyInCreation(beanName);
    }

    protected boolean isActuallyInCreation(String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName);
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        synchronized(this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }

    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized(this.containedBeanMap) {
            Set<String> containedBeans = (Set)this.containedBeanMap.get(containingBeanName);
            if (containedBeans == null) {
                containedBeans = new LinkedHashSet(8);
                this.containedBeanMap.put(containingBeanName, containedBeans);
            }

            ((Set)containedBeans).add(containedBeanName);
        }

        this.registerDependentBean(containedBeanName, containingBeanName);
    }

    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = this.canonicalName(beanName);
        Object dependenciesForBean;
        synchronized(this.dependentBeanMap) {
            dependenciesForBean = (Set)this.dependentBeanMap.get(canonicalName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet(8);
                this.dependentBeanMap.put(canonicalName, (Set<String>)dependenciesForBean);
            }

            ((Set)dependenciesForBean).add(dependentBeanName);
        }

        synchronized(this.dependenciesForBeanMap) {
            dependenciesForBean = (Set)this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet(8);
                this.dependenciesForBeanMap.put(dependentBeanName, (Set<String>)dependenciesForBean);
            }

            ((Set)dependenciesForBean).add(canonicalName);
        }
    }

    protected boolean isDependent(String beanName, String dependentBeanName) {
        synchronized(this.dependentBeanMap) {
            return this.isDependent(beanName, dependentBeanName, (Set)null);
        }
    }

    private boolean isDependent(String beanName, String dependentBeanName, Set<String> alreadySeen) {
        if (alreadySeen != null && ((Set)alreadySeen).contains(beanName)) {
            return false;
        } else {
            String canonicalName = this.canonicalName(beanName);
            Set<String> dependentBeans = (Set)this.dependentBeanMap.get(canonicalName);
            if (dependentBeans == null) {
                return false;
            } else if (dependentBeans.contains(dependentBeanName)) {
                return true;
            } else {
                Iterator var6 = dependentBeans.iterator();

                String transitiveDependency;
                do {
                    if (!var6.hasNext()) {
                        return false;
                    }

                    transitiveDependency = (String)var6.next();
                    if (alreadySeen == null) {
                        alreadySeen = new HashSet();
                    }

                    ((Set)alreadySeen).add(beanName);
                } while(!this.isDependent(transitiveDependency, dependentBeanName, (Set)alreadySeen));

                return true;
            }
        }
    }

    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    public String[] getDependentBeans(String beanName) {
        Set<String> dependentBeans = (Set)this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        } else {
            synchronized(this.dependentBeanMap) {
                return StringUtils.toStringArray(dependentBeans);
            }
        }
    }

    public String[] getDependenciesForBean(String beanName) {
        Set<String> dependenciesForBean = (Set)this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        } else {
            synchronized(this.dependenciesForBeanMap) {
                return StringUtils.toStringArray(dependenciesForBean);
            }
        }
    }

    public void destroySingletons() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Destroying singletons in " + this);
        }

        synchronized(this.singletonObjects) {
            this.singletonsCurrentlyInDestruction = true;
        }

        String[] disposableBeanNames;
        synchronized(this.disposableBeans) {
            disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
        }

        for(int i = disposableBeanNames.length - 1; i >= 0; --i) {
            this.destroySingleton(disposableBeanNames[i]);
        }

        this.containedBeanMap.clear();
        this.dependentBeanMap.clear();
        this.dependenciesForBeanMap.clear();
        this.clearSingletonCache();
    }

    protected void clearSingletonCache() {
        synchronized(this.singletonObjects) {
            this.singletonObjects.clear();
            this.singletonFactories.clear();
            this.earlySingletonObjects.clear();
            this.registeredSingletons.clear();
            this.singletonsCurrentlyInDestruction = false;
        }
    }

    public void destroySingleton(String beanName) {
        this.removeSingleton(beanName);
        DisposableBean disposableBean;
        synchronized(this.disposableBeans) {
            disposableBean = (DisposableBean)this.disposableBeans.remove(beanName);
        }

        this.destroyBean(beanName, disposableBean);
    }

    protected void destroyBean(String beanName, DisposableBean bean) {
        Set dependencies;
        synchronized(this.dependentBeanMap) {
            dependencies = (Set)this.dependentBeanMap.remove(beanName);
        }

        if (dependencies != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }

            Iterator var4 = dependencies.iterator();

            while(var4.hasNext()) {
                String dependentBeanName = (String)var4.next();
                this.destroySingleton(dependentBeanName);
            }
        }

        if (bean != null) {
            try {
                bean.destroy();
            } catch (Throwable var11) {
                this.logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", var11);
            }
        }

        Set containedBeans;
        synchronized(this.containedBeanMap) {
            containedBeans = (Set)this.containedBeanMap.remove(beanName);
        }

        if (containedBeans != null) {
            Iterator var15 = containedBeans.iterator();

            while(var15.hasNext()) {
                String containedBeanName = (String)var15.next();
                this.destroySingleton(containedBeanName);
            }
        }

        synchronized(this.dependentBeanMap) {
            Iterator it = this.dependentBeanMap.entrySet().iterator();

            while(true) {
                if (!it.hasNext()) {
                    break;
                }

                Map.Entry<String, Set<String>> entry = (Map.Entry)it.next();
                Set<String> dependenciesToClean = (Set)entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }

        this.dependenciesForBeanMap.remove(beanName);
    }

    public final Object getSingletonMutex() {
        return this.singletonObjects;
    }
}
