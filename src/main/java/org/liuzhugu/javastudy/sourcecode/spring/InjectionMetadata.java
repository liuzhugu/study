package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class InjectionMetadata {
    private static final Log logger = LogFactory.getLog(InjectionMetadata.class);
    private final Class<?> targetClass;
    private final Collection<InjectionMetadata.InjectedElement> injectedElements;
    private volatile Set<InjectionMetadata.InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectionMetadata.InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        Set<InjectionMetadata.InjectedElement> checkedElements = new LinkedHashSet(this.injectedElements.size());
        Iterator var3 = this.injectedElements.iterator();

        while(var3.hasNext()) {
            InjectionMetadata.InjectedElement element = (InjectionMetadata.InjectedElement)var3.next();
            Member member = element.getMember();
            if (!beanDefinition.isExternallyManagedConfigMember(member)) {
                beanDefinition.registerExternallyManagedConfigMember(member);
                checkedElements.add(element);
                if (logger.isDebugEnabled()) {
                    logger.debug("Registered injected element on class [" + this.targetClass.getName() + "]: " + element);
                }
            }
        }

        this.checkedElements = checkedElements;
    }

    public void inject(Object target, String beanName, PropertyValues pvs) throws Throwable {
        Collection<InjectionMetadata.InjectedElement> elementsToIterate = this.checkedElements != null ? this.checkedElements : this.injectedElements;
        if (!((Collection)elementsToIterate).isEmpty()) {
            boolean debug = logger.isDebugEnabled();

            InjectionMetadata.InjectedElement element;
            for(Iterator var6 = ((Collection)elementsToIterate).iterator(); var6.hasNext(); element.inject(target, beanName, pvs)) {
                element = (InjectionMetadata.InjectedElement)var6.next();
                if (debug) {
                    logger.debug("Processing injected element of bean '" + beanName + "': " + element);
                }
            }
        }

    }

    public void clear(PropertyValues pvs) {
        Collection<InjectionMetadata.InjectedElement> elementsToIterate = this.checkedElements != null ? this.checkedElements : this.injectedElements;
        if (!((Collection)elementsToIterate).isEmpty()) {
            Iterator var3 = ((Collection)elementsToIterate).iterator();

            while(var3.hasNext()) {
                InjectionMetadata.InjectedElement element = (InjectionMetadata.InjectedElement)var3.next();
                element.clearPropertySkipping(pvs);
            }
        }

    }

    public static boolean needsRefresh(InjectionMetadata metadata, Class<?> clazz) {
        return metadata == null || metadata.targetClass != clazz;
    }

    public abstract static class InjectedElement {
        protected final Member member;
        protected final boolean isField;
        protected final PropertyDescriptor pd;
        protected volatile Boolean skip;

        protected InjectedElement(Member member, PropertyDescriptor pd) {
            this.member = member;
            this.isField = member instanceof Field;
            this.pd = pd;
        }

        public final Member getMember() {
            return this.member;
        }

        protected final Class<?> getResourceType() {
            if (this.isField) {
                return ((Field)this.member).getType();
            } else {
                return this.pd != null ? this.pd.getPropertyType() : ((Method)this.member).getParameterTypes()[0];
            }
        }

        protected final void checkResourceType(Class<?> resourceType) {
            Class fieldType;
            if (this.isField) {
                fieldType = ((Field)this.member).getType();
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            } else {
                fieldType = this.pd != null ? this.pd.getPropertyType() : ((Method)this.member).getParameterTypes()[0];
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified parameter type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }

        }

        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            if (this.isField) {
                Field field = (Field)this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, this.getResourceToInject(target, requestingBeanName));
            } else {
                if (this.checkPropertySkipping(pvs)) {
                    return;
                }

                try {
                    Method method = (Method)this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, this.getResourceToInject(target, requestingBeanName));
                } catch (InvocationTargetException var5) {
                    throw var5.getTargetException();
                }
            }

        }

        protected boolean checkPropertySkipping(PropertyValues pvs) {
            if (this.skip != null) {
                return this.skip;
            } else if (pvs == null) {
                this.skip = false;
                return false;
            } else {
                synchronized(pvs) {
                    if (this.skip != null) {
                        return this.skip;
                    } else {
                        if (this.pd != null) {
                            if (pvs.contains(this.pd.getName())) {
                                this.skip = true;
                                return true;
                            }

                            if (pvs instanceof MutablePropertyValues) {
                                ((MutablePropertyValues)pvs).registerProcessedProperty(this.pd.getName());
                            }
                        }

                        this.skip = false;
                        return false;
                    }
                }
            }
        }

        protected void clearPropertySkipping(PropertyValues pvs) {
            if (pvs != null) {
                synchronized(pvs) {
                    if (Boolean.FALSE.equals(this.skip) && this.pd != null && pvs instanceof MutablePropertyValues) {
                        ((MutablePropertyValues)pvs).clearProcessedProperty(this.pd.getName());
                    }

                }
            }
        }

        protected Object getResourceToInject(Object target, String requestingBeanName) {
            return null;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof InjectionMetadata.InjectedElement)) {
                return false;
            } else {
                InjectionMetadata.InjectedElement otherElement = (InjectionMetadata.InjectedElement)other;
                return this.member.equals(otherElement.member);
            }
        }

        public int hashCode() {
            return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
        }

        public String toString() {
            return this.getClass().getSimpleName() + " for " + this.member;
        }
    }
}
