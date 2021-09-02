package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.UsesJava8;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedActionException;
import java.util.*;

public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
    private static final Log logger = LogFactory.getLog(AbstractNestablePropertyAccessor.class);
    private static Class<?> javaUtilOptionalClass = null;
    private int autoGrowCollectionLimit;
    Object wrappedObject;
    private String nestedPath;
    Object rootObject;
    private Map<String, AbstractNestablePropertyAccessor> nestedPropertyAccessors;

    protected AbstractNestablePropertyAccessor() {
        this(true);
    }

    protected AbstractNestablePropertyAccessor(boolean registerDefaultEditors) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        if (registerDefaultEditors) {
            this.registerDefaultEditors();
        }

        this.typeConverterDelegate = new TypeConverterDelegate(this);
    }

    protected AbstractNestablePropertyAccessor(Object object) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        this.registerDefaultEditors();
        this.setWrappedInstance(object);
    }

    protected AbstractNestablePropertyAccessor(Class<?> clazz) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        this.registerDefaultEditors();
        this.setWrappedInstance(BeanUtils.instantiateClass(clazz));
    }

    protected AbstractNestablePropertyAccessor(Object object, String nestedPath, Object rootObject) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        this.registerDefaultEditors();
        this.setWrappedInstance(object, nestedPath, rootObject);
    }

    protected AbstractNestablePropertyAccessor(Object object, String nestedPath, AbstractNestablePropertyAccessor parent) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        this.setWrappedInstance(object, nestedPath, parent.getWrappedInstance());
        this.setExtractOldValueForEditor(parent.isExtractOldValueForEditor());
        this.setAutoGrowNestedPaths(parent.isAutoGrowNestedPaths());
        this.setAutoGrowCollectionLimit(parent.getAutoGrowCollectionLimit());
        this.setConversionService(parent.getConversionService());
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    public void setWrappedInstance(Object object) {
        this.setWrappedInstance(object, "", (Object)null);
    }

    public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
        Assert.notNull(object, "Target object must not be null");
        if (object.getClass() == javaUtilOptionalClass) {
            this.wrappedObject = AbstractNestablePropertyAccessor.OptionalUnwrapper.unwrap(object);
        } else {
            this.wrappedObject = object;
        }

        this.nestedPath = nestedPath != null ? nestedPath : "";
        this.rootObject = !"".equals(this.nestedPath) ? rootObject : this.wrappedObject;
        this.nestedPropertyAccessors = null;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
    }

    public final Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return this.wrappedObject != null ? this.wrappedObject.getClass() : null;
    }

    public final String getNestedPath() {
        return this.nestedPath;
    }

    public final Object getRootInstance() {
        return this.rootObject;
    }

    public final Class<?> getRootClass() {
        return this.rootObject != null ? this.rootObject.getClass() : null;
    }

    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        AbstractNestablePropertyAccessor nestedPa;
        try {
            nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        } catch (NotReadablePropertyException var5) {
            throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", var5);
        }

        PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
        nestedPa.setPropertyValue(tokens, new PropertyValue(propertyName, value));
    }

    public void setPropertyValue(PropertyValue pv) throws BeansException {
        PropertyTokenHolder tokens = (PropertyTokenHolder)pv.resolvedTokens;
        if (tokens == null) {
            String propertyName = pv.getName();

            AbstractNestablePropertyAccessor nestedPa;
            try {
                nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
            } catch (NotReadablePropertyException var6) {
                throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", var6);
            }

            tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
            if (nestedPa == this) {
                pv.getOriginalPropertyValue().resolvedTokens = tokens;
            }

            nestedPa.setPropertyValue(tokens, pv);
        } else {
            this.setPropertyValue(tokens, pv);
        }

    }

    protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
        if (tokens.keys != null) {
            this.processKeyedProperty(tokens, pv);
        } else {
            this.processLocalProperty(tokens, pv);
        }

    }

    private void processKeyedProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        Object propValue = this.getPropertyHoldingValue(tokens);
        String lastKey = tokens.keys[tokens.keys.length - 1];
        PropertyHandler ph;
        Class requiredType;
        Object convertedValue;
        Object newArray;
        if (propValue.getClass().isArray()) {
            ph = this.getLocalPropertyHandler(tokens.actualName);
            requiredType = propValue.getClass().getComponentType();
            int arrayIndex = Integer.parseInt(lastKey);
            Object oldValue = null;

            try {
                if (this.isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
                    oldValue = Array.get(propValue, arrayIndex);
                }

                convertedValue = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                int length = Array.getLength(propValue);
                if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
                    Class<?> componentType = propValue.getClass().getComponentType();
                    newArray = Array.newInstance(componentType, arrayIndex + 1);
                    System.arraycopy(propValue, 0, newArray, 0, length);
                    this.setPropertyValue(tokens.actualName, newArray);
                    propValue = this.getPropertyValue(tokens.actualName);
                }

                Array.set(propValue, arrayIndex, convertedValue);
            } catch (IndexOutOfBoundsException var16) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid array index in property path '" + tokens.canonicalName + "'", var16);
            }
        } else {
            if (propValue instanceof List) {
                ph = this.getPropertyHandler(tokens.actualName);
                requiredType = ph.getCollectionType(tokens.keys.length);
                List<Object> list = (List)propValue;
                int index = Integer.parseInt(lastKey);
                convertedValue = null;
                if (this.isExtractOldValueForEditor() && index < list.size()) {
                    convertedValue = list.get(index);
                }

                convertedValue = this.convertIfNecessary(tokens.canonicalName, convertedValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                int size = list.size();
                if (index >= size && index < this.autoGrowCollectionLimit) {
                    for(int i = size; i < index; ++i) {
                        try {
                            list.add((Object)null);
                        } catch (NullPointerException var15) {
                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + tokens.canonicalName + "': List does not support filling up gaps with null elements");
                        }
                    }

                    list.add(convertedValue);
                } else {
                    try {
                        list.set(index, convertedValue);
                    } catch (IndexOutOfBoundsException var14) {
                        throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid list index in property path '" + tokens.canonicalName + "'", var14);
                    }
                }
            } else {
                if (!(propValue instanceof Map)) {
                    throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Property referenced in indexed property path '" + tokens.canonicalName + "' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
                }

                ph = this.getLocalPropertyHandler(tokens.actualName);
                requiredType = ph.getMapKeyType(tokens.keys.length);
                Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
                Map<Object, Object> map = (Map)propValue;
                TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(requiredType);
                convertedValue = this.convertIfNecessary((String)null, (Object)null, lastKey, requiredType, typeDescriptor);
                Object oldValue = null;
                if (this.isExtractOldValueForEditor()) {
                    oldValue = map.get(convertedValue);
                }

                newArray = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), mapValueType, ph.nested(tokens.keys.length));
                map.put(convertedValue, newArray);
            }
        }

    }

    private Object getPropertyHoldingValue(PropertyTokenHolder tokens) {
        PropertyTokenHolder getterTokens = new PropertyTokenHolder();
        getterTokens.canonicalName = tokens.canonicalName;
        getterTokens.actualName = tokens.actualName;
        getterTokens.keys = new String[tokens.keys.length - 1];
        System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);

        Object propValue;
        try {
            propValue = this.getPropertyValue(getterTokens);
        } catch (NotReadablePropertyException var5) {
            throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "'", var5);
        }

        if (propValue == null) {
            if (!this.isAutoGrowNestedPaths()) {
                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "': returned null");
            }

            int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
            getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
            propValue = this.setDefaultValue(getterTokens);
        }

        return propValue;
    }

    private void processLocalProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);
        if (ph != null && ph.isWritable()) {
            Object oldValue = null;

            PropertyChangeEvent propertyChangeEvent;
            try {
                Object originalValue = pv.getValue();
                Object valueToApply = originalValue;
                if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                    if (pv.isConverted()) {
                        valueToApply = pv.getConvertedValue();
                    } else {
                        if (this.isExtractOldValueForEditor() && ph.isReadable()) {
                            try {
                                oldValue = ph.getValue();
                            } catch (Exception var8) {
                                Exception ex = var8;
                                if (var8 instanceof PrivilegedActionException) {
                                    ex = ((PrivilegedActionException)var8).getException();
                                }

                                if (logger.isDebugEnabled()) {
                                    logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
                                }
                            }
                        }

                        valueToApply = this.convertForProperty(tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());
                    }

                    pv.getOriginalPropertyValue().conversionNecessary = valueToApply != originalValue;
                }

                ph.setValue(this.wrappedObject, valueToApply);
            } catch (TypeMismatchException var9) {
                throw var9;
            } catch (InvocationTargetException var10) {
                propertyChangeEvent = new PropertyChangeEvent(this.rootObject, this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
                if (var10.getTargetException() instanceof ClassCastException) {
                    throw new TypeMismatchException(propertyChangeEvent, ph.getPropertyType(), var10.getTargetException());
                } else {
                    Throwable cause = var10.getTargetException();
                    if (cause instanceof UndeclaredThrowableException) {
                        cause = cause.getCause();
                    }

                    throw new MethodInvocationException(propertyChangeEvent, cause);
                }
            } catch (Exception var11) {
                propertyChangeEvent = new PropertyChangeEvent(this.rootObject, this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
                throw new MethodInvocationException(propertyChangeEvent, var11);
            }
        } else if (pv.isOptional()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring optional value for property '" + tokens.actualName + "' - property not found on bean class [" + this.getRootClass().getName() + "]");
            }

        } else {
            throw this.createNotWritablePropertyException(tokens.canonicalName);
        }
    }

    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.getPropertyType();
            }

            Object value = this.getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }

            Class<?> editorType = this.guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
        } catch (InvalidPropertyException var5) {
        }

        return null;
    }

    public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
        try {
            AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
            String finalPath = this.getFinalPath(nestedPa, propertyName);
            PropertyTokenHolder tokens = this.getPropertyNameTokens(finalPath);
            PropertyHandler ph = nestedPa.getLocalPropertyHandler(tokens.actualName);
            if (ph != null) {
                if (tokens.keys != null) {
                    if (ph.isReadable() || ph.isWritable()) {
                        return ph.nested(tokens.keys.length);
                    }
                } else if (ph.isReadable() || ph.isWritable()) {
                    return ph.toTypeDescriptor();
                }
            }
        } catch (InvalidPropertyException var6) {
        }

        return null;
    }

    public boolean isReadableProperty(String propertyName) {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isReadable();
            } else {
                this.getPropertyValue(propertyName);
                return true;
            }
        } catch (InvalidPropertyException var3) {
            return false;
        }
    }

    public boolean isWritableProperty(String propertyName) {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isWritable();
            } else {
                this.getPropertyValue(propertyName);
                return true;
            }
        } catch (InvalidPropertyException var3) {
            return false;
        }
    }

    private Object convertIfNecessary(String propertyName, Object oldValue, Object newValue, Class<?> requiredType, TypeDescriptor td) throws TypeMismatchException {
        PropertyChangeEvent pce;
        try {
            return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
        } catch (ConverterNotFoundException var8) {
            pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, td.getType(), var8);
        } catch (ConversionException var9) {
            pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, var9);
        } catch (IllegalStateException var10) {
            pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, requiredType, var10);
        } catch (IllegalArgumentException var11) {
            pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, var11);
        }
    }

    protected Object convertForProperty(String propertyName, Object oldValue, Object newValue, TypeDescriptor td) throws TypeMismatchException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
    }

    public Object getPropertyValue(String propertyName) throws BeansException {
        AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
        return nestedPa.getPropertyValue(tokens);
    }

    protected Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
        String propertyName = tokens.canonicalName;
        String actualName = tokens.actualName;
        PropertyHandler ph = this.getLocalPropertyHandler(actualName);
        if (ph != null && ph.isReadable()) {
            try {
                Object value = ph.getValue();
                if (tokens.keys != null) {
                    if (value == null) {
                        if (!this.isAutoGrowNestedPaths()) {
                            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                        }

                        value = this.setDefaultValue(tokens.actualName);
                    }

                    String indexedPropertyName = tokens.actualName;

                    for(int i = 0; i < tokens.keys.length; ++i) {
                        String key = tokens.keys[i];
                        if (value == null) {
                            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                        }

                        int index;
                        if (value.getClass().isArray()) {
                            index = Integer.parseInt(key);
                            value = this.growArrayIfNecessary(value, index, indexedPropertyName);
                            value = Array.get(value, index);
                        } else if (value instanceof List) {
                            index = Integer.parseInt(key);
                            List<Object> list = (List)value;
                            this.growCollectionIfNecessary(list, index, indexedPropertyName, ph, i + 1);
                            value = list.get(index);
                        } else if (value instanceof Set) {
                            Set<Object> set = (Set)value;
                            index = Integer.parseInt(key);
                            if (index < 0 || index >= set.size()) {
                                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Cannot get element with index " + index + " from Set of size " + set.size() + ", accessed using property path '" + propertyName + "'");
                            }

                            Iterator<Object> it = set.iterator();

                            for(int j = 0; it.hasNext(); ++j) {
                                Object elem = it.next();
                                if (j == index) {
                                    value = elem;
                                    break;
                                }
                            }
                        } else {
                            if (!(value instanceof Map)) {
                                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
                            }

                            Map<Object, Object> map = (Map)value;
                            Class<?> mapKeyType = ph.getResolvableType().getNested(i + 1).asMap().resolveGeneric(new int[]{0});
                            TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
                            Object convertedMapKey = this.convertIfNecessary((String)null, (Object)null, key, mapKeyType, typeDescriptor);
                            value = map.get(convertedMapKey);
                        }

                        indexedPropertyName = indexedPropertyName + "[" + key + "]";
                    }
                }

                return value;
            } catch (IndexOutOfBoundsException var14) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Index of out of bounds in property path '" + propertyName + "'", var14);
            } catch (NumberFormatException var15) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", var15);
            } catch (TypeMismatchException var16) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", var16);
            } catch (InvocationTargetException var17) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName + "' threw exception", var17);
            } catch (Exception var18) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '" + actualName + "' threw exception", var18);
            }
        } else {
            throw new NotReadablePropertyException(this.getRootClass(), this.nestedPath + propertyName);
        }
    }

    protected PropertyHandler getPropertyHandler(String propertyName) throws BeansException {
        Assert.notNull(propertyName, "Property name must not be null");
        AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        return nestedPa.getLocalPropertyHandler(this.getFinalPath(nestedPa, propertyName));
    }

    protected abstract PropertyHandler getLocalPropertyHandler(String var1);

    protected abstract AbstractNestablePropertyAccessor newNestedPropertyAccessor(Object var1, String var2);

    protected abstract NotWritablePropertyException createNotWritablePropertyException(String var1);

    private Object growArrayIfNecessary(Object array, int index, String name) {
        if (!this.isAutoGrowNestedPaths()) {
            return array;
        } else {
            int length = Array.getLength(array);
            if (index >= length && index < this.autoGrowCollectionLimit) {
                Class<?> componentType = array.getClass().getComponentType();
                Object newArray = Array.newInstance(componentType, index + 1);
                System.arraycopy(array, 0, newArray, 0, length);

                for(int i = length; i < Array.getLength(newArray); ++i) {
                    Array.set(newArray, i, this.newValue(componentType, (TypeDescriptor)null, name));
                }

                this.setPropertyValue(name, newArray);
                return this.getPropertyValue(name);
            } else {
                return array;
            }
        }
    }

    private void growCollectionIfNecessary(Collection<Object> collection, int index, String name, PropertyHandler ph, int nestingLevel) {
        if (this.isAutoGrowNestedPaths()) {
            int size = collection.size();
            if (index >= size && index < this.autoGrowCollectionLimit) {
                Class<?> elementType = ph.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
                if (elementType != null) {
                    for(int i = collection.size(); i < index + 1; ++i) {
                        collection.add(this.newValue(elementType, (TypeDescriptor)null, name));
                    }
                }
            }

        }
    }

    protected String getFinalPath(AbstractNestablePropertyAccessor pa, String nestedPath) {
        return pa == this ? nestedPath : nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
    }

    protected AbstractNestablePropertyAccessor getPropertyAccessorForPropertyPath(String propertyPath) {
        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        if (pos > -1) {
            String nestedProperty = propertyPath.substring(0, pos);
            String nestedPath = propertyPath.substring(pos + 1);
            AbstractNestablePropertyAccessor nestedPa = this.getNestedPropertyAccessor(nestedProperty);
            return nestedPa.getPropertyAccessorForPropertyPath(nestedPath);
        } else {
            return this;
        }
    }

    private AbstractNestablePropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
        if (this.nestedPropertyAccessors == null) {
            this.nestedPropertyAccessors = new HashMap();
        }

        PropertyTokenHolder tokens = this.getPropertyNameTokens(nestedProperty);
        String canonicalName = tokens.canonicalName;
        Object value = this.getPropertyValue(tokens);
        if (value == null || value.getClass() == javaUtilOptionalClass && AbstractNestablePropertyAccessor.OptionalUnwrapper.isEmpty(value)) {
            if (!this.isAutoGrowNestedPaths()) {
                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + canonicalName);
            }

            value = this.setDefaultValue(tokens);
        }

        AbstractNestablePropertyAccessor nestedPa = (AbstractNestablePropertyAccessor)this.nestedPropertyAccessors.get(canonicalName);
        if (nestedPa != null && nestedPa.getWrappedInstance() == (value.getClass() == javaUtilOptionalClass ? AbstractNestablePropertyAccessor.OptionalUnwrapper.unwrap(value) : value)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Using cached nested property accessor for property '" + canonicalName + "'");
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Creating new nested " + this.getClass().getSimpleName() + " for property '" + canonicalName + "'");
            }

            nestedPa = this.newNestedPropertyAccessor(value, this.nestedPath + canonicalName + ".");
            this.copyDefaultEditorsTo(nestedPa);
            this.copyCustomEditorsTo(nestedPa, canonicalName);
            this.nestedPropertyAccessors.put(canonicalName, nestedPa);
        }

        return nestedPa;
    }

    private Object setDefaultValue(String propertyName) {
        PropertyTokenHolder tokens = new PropertyTokenHolder();
        tokens.actualName = propertyName;
        tokens.canonicalName = propertyName;
        return this.setDefaultValue(tokens);
    }

    private Object setDefaultValue(PropertyTokenHolder tokens) {
        PropertyValue pv = this.createDefaultPropertyValue(tokens);
        this.setPropertyValue(tokens, pv);
        return this.getPropertyValue(tokens);
    }

    private PropertyValue createDefaultPropertyValue(PropertyTokenHolder tokens) {
        TypeDescriptor desc = this.getPropertyTypeDescriptor(tokens.canonicalName);
        Class<?> type = desc.getType();
        if (type == null) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Could not determine property type for auto-growing a default value");
        } else {
            Object defaultValue = this.newValue(type, desc, tokens.canonicalName);
            return new PropertyValue(tokens.canonicalName, defaultValue);
        }
    }

    private Object newValue(Class<?> type, TypeDescriptor desc, String name) {
        try {
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if (componentType.isArray()) {
                    Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
                    return array;
                } else {
                    return Array.newInstance(componentType, 0);
                }
            } else {
                TypeDescriptor keyDesc;
                if (Collection.class.isAssignableFrom(type)) {
                    keyDesc = desc != null ? desc.getElementTypeDescriptor() : null;
                    return CollectionFactory.createCollection(type, keyDesc != null ? keyDesc.getType() : null, 16);
                } else if (Map.class.isAssignableFrom(type)) {
                    keyDesc = desc != null ? desc.getMapKeyTypeDescriptor() : null;
                    return CollectionFactory.createMap(type, keyDesc != null ? keyDesc.getType() : null, 16);
                } else {
                    return BeanUtils.instantiate(type);
                }
            }
        } catch (Throwable var6) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + name, "Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path", var6);
        }
    }

    private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
        PropertyTokenHolder tokens = new PropertyTokenHolder();
        String actualName = null;
        List<String> keys = new ArrayList(2);
        int searchIndex = 0;

        while(true) {
            int keyStart;
            int keyEnd;
            do {
                do {
                    if (searchIndex == -1) {
                        tokens.actualName = actualName != null ? actualName : propertyName;
                        tokens.canonicalName = tokens.actualName;
                        if (!keys.isEmpty()) {
                            tokens.canonicalName = tokens.canonicalName + "[" + StringUtils.collectionToDelimitedString(keys, "][") + "]";
                            tokens.keys = StringUtils.toStringArray(keys);
                        }

                        return tokens;
                    }

                    keyStart = propertyName.indexOf("[", searchIndex);
                    searchIndex = -1;
                } while(keyStart == -1);

                keyEnd = propertyName.indexOf("]", keyStart + "[".length());
            } while(keyEnd == -1);

            if (actualName == null) {
                actualName = propertyName.substring(0, keyStart);
            }

            String key = propertyName.substring(keyStart + "[".length(), keyEnd);
            if (key.length() > 1 && key.startsWith("'") && key.endsWith("'") || key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }

            keys.add(key);
            searchIndex = keyEnd + "]".length();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        if (this.wrappedObject != null) {
            sb.append(": wrapping object [").append(ObjectUtils.identityToString(this.wrappedObject)).append("]");
        } else {
            sb.append(": no wrapped object set");
        }

        return sb.toString();
    }

    static {
        try {
            javaUtilOptionalClass = ClassUtils.forName("java.util.Optional", AbstractNestablePropertyAccessor.class.getClassLoader());
        } catch (ClassNotFoundException var1) {
        }

    }

    @UsesJava8
    private static class OptionalUnwrapper {
        private OptionalUnwrapper() {
        }

        public static Object unwrap(Object optionalObject) {
            Optional<?> optional = (Optional)optionalObject;
            Assert.isTrue(optional.isPresent(), "Optional value must be present");
            Object result = optional.get();
            Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
            return result;
        }

        public static boolean isEmpty(Object optionalObject) {
            return !((Optional)optionalObject).isPresent();
        }
    }

    protected static class PropertyTokenHolder {
        public String canonicalName;
        public String actualName;
        public String[] keys;

        protected PropertyTokenHolder() {
        }
    }

    protected abstract static class PropertyHandler {
        private final Class<?> propertyType;
        private final boolean readable;
        private final boolean writable;

        public PropertyHandler(Class<?> propertyType, boolean readable, boolean writable) {
            this.propertyType = propertyType;
            this.readable = readable;
            this.writable = writable;
        }

        public Class<?> getPropertyType() {
            return this.propertyType;
        }

        public boolean isReadable() {
            return this.readable;
        }

        public boolean isWritable() {
            return this.writable;
        }

        public abstract TypeDescriptor toTypeDescriptor();

        public abstract ResolvableType getResolvableType();

        public Class<?> getMapKeyType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[]{0});
        }

        public Class<?> getMapValueType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[]{1});
        }

        public Class<?> getCollectionType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
        }

        public abstract TypeDescriptor nested(int var1);

        public abstract Object getValue() throws Exception;

        public abstract void setValue(Object var1, Object var2) throws Exception;
    }
}