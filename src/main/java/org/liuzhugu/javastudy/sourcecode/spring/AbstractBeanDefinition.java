package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.*;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition, Cloneable {
    public static final String SCOPE_DEFAULT = "";
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    /** @deprecated */
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final int DEPENDENCY_CHECK_NONE = 0;
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;
    public static final int DEPENDENCY_CHECK_SIMPLE = 2;
    public static final int DEPENDENCY_CHECK_ALL = 3;
    public static final String INFER_METHOD = "(inferred)";
    private volatile Object beanClass;
    private String scope;
    private boolean abstractFlag;
    private boolean lazyInit;
    private int autowireMode;
    private int dependencyCheck;
    private String[] dependsOn;
    private boolean autowireCandidate;
    private boolean primary;
    private final Map<String, AutowireCandidateQualifier> qualifiers;
    private boolean nonPublicAccessAllowed;
    private boolean lenientConstructorResolution;
    private String factoryBeanName;
    private String factoryMethodName;
    private ConstructorArgumentValues constructorArgumentValues;
    private MutablePropertyValues propertyValues;
    private MethodOverrides methodOverrides;
    private String initMethodName;
    private String destroyMethodName;
    private boolean enforceInitMethod;
    private boolean enforceDestroyMethod;
    private boolean synthetic;
    private int role;
    private String description;
    private Resource resource;

    protected AbstractBeanDefinition() {
        this((ConstructorArgumentValues)null, (MutablePropertyValues)null);
    }

    protected AbstractBeanDefinition(ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        this.scope = "";
        this.abstractFlag = false;
        this.lazyInit = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap(0);
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        this.setConstructorArgumentValues(cargs);
        this.setPropertyValues(pvs);
    }

    protected AbstractBeanDefinition(BeanDefinition original) {
        this.scope = "";
        this.abstractFlag = false;
        this.lazyInit = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap(0);
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        this.setParentName(original.getParentName());
        this.setBeanClassName(original.getBeanClassName());
        this.setScope(original.getScope());
        this.setAbstract(original.isAbstract());
        this.setLazyInit(original.isLazyInit());
        this.setFactoryBeanName(original.getFactoryBeanName());
        this.setFactoryMethodName(original.getFactoryMethodName());
        this.setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
        this.setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
        this.setRole(original.getRole());
        this.setSource(original.getSource());
        this.copyAttributesFrom(original);
        if (original instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition)original;
            if (originalAbd.hasBeanClass()) {
                this.setBeanClass(originalAbd.getBeanClass());
            }

            this.setAutowireMode(originalAbd.getAutowireMode());
            this.setDependencyCheck(originalAbd.getDependencyCheck());
            this.setDependsOn(originalAbd.getDependsOn());
            this.setAutowireCandidate(originalAbd.isAutowireCandidate());
            this.setPrimary(originalAbd.isPrimary());
            this.copyQualifiersFrom(originalAbd);
            this.setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            this.setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            this.setInitMethodName(originalAbd.getInitMethodName());
            this.setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            this.setDestroyMethodName(originalAbd.getDestroyMethodName());
            this.setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            this.setSynthetic(originalAbd.isSynthetic());
            this.setResource(originalAbd.getResource());
        } else {
            this.setResourceDescription(original.getResourceDescription());
        }

    }

    public void overrideFrom(BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            this.setBeanClassName(other.getBeanClassName());
        }

        if (StringUtils.hasLength(other.getScope())) {
            this.setScope(other.getScope());
        }

        this.setAbstract(other.isAbstract());
        this.setLazyInit(other.isLazyInit());
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            this.setFactoryBeanName(other.getFactoryBeanName());
        }

        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            this.setFactoryMethodName(other.getFactoryMethodName());
        }

        this.getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
        this.getPropertyValues().addPropertyValues(other.getPropertyValues());
        this.setRole(other.getRole());
        this.setSource(other.getSource());
        this.copyAttributesFrom(other);
        if (other instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition)other;
            if (otherAbd.hasBeanClass()) {
                this.setBeanClass(otherAbd.getBeanClass());
            }

            this.setAutowireMode(otherAbd.getAutowireMode());
            this.setDependencyCheck(otherAbd.getDependencyCheck());
            this.setDependsOn(otherAbd.getDependsOn());
            this.setAutowireCandidate(otherAbd.isAutowireCandidate());
            this.setPrimary(otherAbd.isPrimary());
            this.copyQualifiersFrom(otherAbd);
            this.setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            this.getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            if (StringUtils.hasLength(otherAbd.getInitMethodName())) {
                this.setInitMethodName(otherAbd.getInitMethodName());
                this.setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }

            if (otherAbd.getDestroyMethodName() != null) {
                this.setDestroyMethodName(otherAbd.getDestroyMethodName());
                this.setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }

            this.setSynthetic(otherAbd.isSynthetic());
            this.setResource(otherAbd.getResource());
        } else {
            this.setResourceDescription(other.getResourceDescription());
        }

    }

    public void applyDefaults(BeanDefinitionDefaults defaults) {
        this.setLazyInit(defaults.isLazyInit());
        this.setAutowireMode(defaults.getAutowireMode());
        this.setDependencyCheck(defaults.getDependencyCheck());
        this.setInitMethodName(defaults.getInitMethodName());
        this.setEnforceInitMethod(false);
        this.setDestroyMethodName(defaults.getDestroyMethodName());
        this.setEnforceDestroyMethod(false);
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        return beanClassObject instanceof Class ? ((Class)beanClassObject).getName() : (String)beanClassObject;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        } else if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        } else {
            return (Class)beanClassObject;
        }
    }

    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }

    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = this.getBeanClassName();
        if (className == null) {
            return null;
        } else {
            Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
            this.beanClass = resolvedClass;
            return resolvedClass;
        }
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }

    public boolean isSingleton() {
        return "singleton".equals(this.scope) || "".equals(this.scope);
    }

    public boolean isPrototype() {
        return "prototype".equals(this.scope);
    }

    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    public boolean isAbstract() {
        return this.abstractFlag;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public int getResolvedAutowireMode() {
        if (this.autowireMode == 4) {
            Constructor<?>[] constructors = this.getBeanClass().getConstructors();
            Constructor[] var2 = constructors;
            int var3 = constructors.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Constructor<?> constructor = var2[var4];
                if (constructor.getParameterTypes().length == 0) {
                    return 2;
                }
            }

            return 3;
        } else {
            return this.autowireMode;
        }
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String[] getDependsOn() {
        return this.dependsOn;
    }

    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public void addQualifier(AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }

    public boolean hasQualifier(String typeName) {
        return this.qualifiers.keySet().contains(typeName);
    }

    public AutowireCandidateQualifier getQualifier(String typeName) {
        return (AutowireCandidateQualifier)this.qualifiers.get(typeName);
    }

    public Set<AutowireCandidateQualifier> getQualifiers() {
        return new LinkedHashSet(this.qualifiers.values());
    }

    public void copyQualifiersFrom(AbstractBeanDefinition source) {
        Assert.notNull(source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }

    public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }

    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues();
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgumentValues.isEmpty();
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues != null ? propertyValues : new MutablePropertyValues();
    }

    public MutablePropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides != null ? methodOverrides : new MethodOverrides();
    }

    public MethodOverrides getMethodOverrides() {
        return this.methodOverrides;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setEnforceInitMethod(boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }

    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }

    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public boolean isSynthetic() {
        return this.synthetic;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return this.role;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resource = new DescriptiveResource(resourceDescription);
    }

    public String getResourceDescription() {
        return this.resource != null ? this.resource.getDescription() : null;
    }

    public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }

    public BeanDefinition getOriginatingBeanDefinition() {
        return this.resource instanceof BeanDefinitionResource ? ((BeanDefinitionResource)this.resource).getBeanDefinition() : null;
    }

    public void validate() throws BeanDefinitionValidationException {
        if (!this.getMethodOverrides().isEmpty() && this.getFactoryMethodName() != null) {
            throw new BeanDefinitionValidationException("Cannot combine static factory method with method overrides: the static factory method must create the instance");
        } else {
            if (this.hasBeanClass()) {
                this.prepareMethodOverrides();
            }

        }
    }

    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        MethodOverrides methodOverrides = this.getMethodOverrides();
        if (!methodOverrides.isEmpty()) {
            Set<MethodOverride> overrides = methodOverrides.getOverrides();
            synchronized(overrides) {
                Iterator var4 = overrides.iterator();

                while(var4.hasNext()) {
                    MethodOverride mo = (MethodOverride)var4.next();
                    this.prepareMethodOverride(mo);
                }
            }
        }

    }

    protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
        int count = ClassUtils.getMethodCountForName(this.getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + this.getBeanClassName() + "]");
        } else {
            if (count == 1) {
                mo.setOverloaded(false);
            }

        }
    }

    public Object clone() {
        return this.cloneBeanDefinition();
    }

    public abstract AbstractBeanDefinition cloneBeanDefinition();

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof AbstractBeanDefinition)) {
            return false;
        } else {
            AbstractBeanDefinition that = (AbstractBeanDefinition)other;
            if (!ObjectUtils.nullSafeEquals(this.getBeanClassName(), that.getBeanClassName())) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.scope, that.scope)) {
                return false;
            } else if (this.abstractFlag != that.abstractFlag) {
                return false;
            } else if (this.lazyInit != that.lazyInit) {
                return false;
            } else if (this.autowireMode != that.autowireMode) {
                return false;
            } else if (this.dependencyCheck != that.dependencyCheck) {
                return false;
            } else if (!Arrays.equals(this.dependsOn, that.dependsOn)) {
                return false;
            } else if (this.autowireCandidate != that.autowireCandidate) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers)) {
                return false;
            } else if (this.primary != that.primary) {
                return false;
            } else if (this.nonPublicAccessAllowed != that.nonPublicAccessAllowed) {
                return false;
            } else if (this.lenientConstructorResolution != that.lenientConstructorResolution) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName)) {
                return false;
            } else if (this.enforceInitMethod != that.enforceInitMethod) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName)) {
                return false;
            } else if (this.enforceDestroyMethod != that.enforceDestroyMethod) {
                return false;
            } else if (this.synthetic != that.synthetic) {
                return false;
            } else {
                return this.role != that.role ? false : super.equals(other);
            }
        }
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getBeanClassName());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
        hashCode = 29 * hashCode + super.hashCode();
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("class [");
        sb.append(this.getBeanClassName()).append("]");
        sb.append("; scope=").append(this.scope);
        sb.append("; abstract=").append(this.abstractFlag);
        sb.append("; lazyInit=").append(this.lazyInit);
        sb.append("; autowireMode=").append(this.autowireMode);
        sb.append("; dependencyCheck=").append(this.dependencyCheck);
        sb.append("; autowireCandidate=").append(this.autowireCandidate);
        sb.append("; primary=").append(this.primary);
        sb.append("; factoryBeanName=").append(this.factoryBeanName);
        sb.append("; factoryMethodName=").append(this.factoryMethodName);
        sb.append("; initMethodName=").append(this.initMethodName);
        sb.append("; destroyMethodName=").append(this.destroyMethodName);
        if (this.resource != null) {
            sb.append("; defined in ").append(this.resource.getDescription());
        }

        return sb.toString();
    }
}
