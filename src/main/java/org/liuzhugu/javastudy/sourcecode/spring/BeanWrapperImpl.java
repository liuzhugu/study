package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.*;

public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {
    private CachedIntrospectionResults cachedIntrospectionResults;
    private AccessControlContext acc;

    public BeanWrapperImpl() {
        this(true);
    }

    public BeanWrapperImpl(boolean registerDefaultEditors) {
        super(registerDefaultEditors);
    }

    public BeanWrapperImpl(Object object) {
        super(object);
    }

    public BeanWrapperImpl(Class<?> clazz) {
        super(clazz);
    }

    public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
        super(object, nestedPath, rootObject);
    }

    private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
        super(object, nestedPath, parent);
        this.setSecurityContext(parent.acc);
    }

    public void setBeanInstance(Object object) {
        this.wrappedObject = object;
        this.rootObject = object;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
        this.setIntrospectionClass(object.getClass());
    }

    public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
        super.setWrappedInstance(object, nestedPath, rootObject);
        this.setIntrospectionClass(this.getWrappedClass());
    }

    protected void setIntrospectionClass(Class<?> clazz) {
        if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
            this.cachedIntrospectionResults = null;
        }

    }

    private CachedIntrospectionResults getCachedIntrospectionResults() {
        Assert.state(this.getWrappedInstance() != null, "BeanWrapper does not hold a bean instance");
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(this.getWrappedClass());
        }

        return this.cachedIntrospectionResults;
    }

    public void setSecurityContext(AccessControlContext acc) {
        this.acc = acc;
    }

    public AccessControlContext getSecurityContext() {
        return this.acc;
    }

    public Object convertForProperty(Object value, String propertyName) throws TypeMismatchException {
        CachedIntrospectionResults cachedIntrospectionResults = this.getCachedIntrospectionResults();
        PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        } else {
            TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
            if (td == null) {
                td = cachedIntrospectionResults.addTypeDescriptor(pd, new TypeDescriptor(this.property(pd)));
            }

            return this.convertForProperty(propertyName, (Object)null, value, td);
        }
    }

    private Property property(PropertyDescriptor pd) {
        GenericTypeAwarePropertyDescriptor gpd = (GenericTypeAwarePropertyDescriptor)pd;
        return new Property(gpd.getBeanClass(), gpd.getReadMethod(), gpd.getWriteMethod(), gpd.getName());
    }

    protected BeanWrapperImpl.BeanPropertyHandler getLocalPropertyHandler(String propertyName) {
        PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        return pd != null ? new BeanWrapperImpl.BeanPropertyHandler(pd) : null;
    }

    protected BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }

    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forProperty(propertyName, this.getRootClass());
        throw new NotWritablePropertyException(this.getRootClass(), this.getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.getCachedIntrospectionResults().getPropertyDescriptors();
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
        BeanWrapperImpl nestedBw = (BeanWrapperImpl)this.getPropertyAccessorForPropertyPath(propertyName);
        String finalPath = this.getFinalPath(nestedBw, propertyName);
        PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(finalPath);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        } else {
            return pd;
        }
    }

    private class BeanPropertyHandler extends PropertyHandler {
        private final PropertyDescriptor pd;

        public BeanPropertyHandler(PropertyDescriptor pd) {
            super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
            this.pd = pd;
        }

        public ResolvableType getResolvableType() {
            return ResolvableType.forMethodReturnType(this.pd.getReadMethod());
        }

        public TypeDescriptor toTypeDescriptor() {
            return new TypeDescriptor(BeanWrapperImpl.this.property(this.pd));
        }

        public TypeDescriptor nested(int level) {
            return TypeDescriptor.nested(BeanWrapperImpl.this.property(this.pd), level);
        }

        public Object getValue() throws Exception {
            final Method readMethod = this.pd.getReadMethod();
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) && !readMethod.isAccessible()) {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            readMethod.setAccessible(true);
                            return null;
                        }
                    });
                } else {
                    readMethod.setAccessible(true);
                }
            }

            if (System.getSecurityManager() != null) {
                try {
                    return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[])null);
                        }
                    }, BeanWrapperImpl.this.acc);
                } catch (PrivilegedActionException var3) {
                    throw var3.getException();
                }
            } else {
                return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[])null);
            }
        }

        public void setValue(final Object object, Object valueToApply) throws Exception {
            final Method writeMethod = this.pd instanceof GenericTypeAwarePropertyDescriptor ? ((GenericTypeAwarePropertyDescriptor)this.pd).getWriteMethodForActualAccess() : this.pd.getWriteMethod();
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers()) && !writeMethod.isAccessible()) {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            writeMethod.setAccessible(true);
                            return null;
                        }
                    });
                } else {
                    writeMethod.setAccessible(true);
                }
            }

            final Object value = valueToApply;
            if (System.getSecurityManager() != null) {
                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        public Object run() throws Exception {
                            writeMethod.invoke(object, value);
                            return null;
                        }
                    }, BeanWrapperImpl.this.acc);
                } catch (PrivilegedActionException var6) {
                    throw var6.getException();
                }
            } else {
                writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), valueToApply);
            }

        }
    }
}
