package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

class TypeConverterDelegate {
    private static final Log logger = LogFactory.getLog(TypeConverterDelegate.class);
    private static Object javaUtilOptionalEmpty = null;
    private final PropertyEditorRegistrySupport propertyEditorRegistry;
    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, (Object)null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    public <T> T convertIfNecessary(Object newValue, Class<T> requiredType, MethodParameter methodParam) throws IllegalArgumentException {
        return this.convertIfNecessary((String)null, (Object)null, newValue, requiredType, methodParam != null ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType));
    }

    public <T> T convertIfNecessary(Object newValue, Class<T> requiredType, Field field) throws IllegalArgumentException {
        return this.convertIfNecessary((String)null, (Object)null, newValue, requiredType, field != null ? new TypeDescriptor(field) : TypeDescriptor.valueOf(requiredType));
    }

    public <T> T convertIfNecessary(String propertyName, Object oldValue, Object newValue, Class<T> requiredType) throws IllegalArgumentException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    public <T> T convertIfNecessary(String propertyName, Object oldValue, Object newValue, Class<T> requiredType, TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
        ConversionFailedException conversionAttemptEx = null;
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                try {
                    return (T)conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                } catch (ConversionFailedException var14) {
                    conversionAttemptEx = var14;
                }
            }
        }

        Object convertedValue = newValue;
        if (editor != null || requiredType != null && !ClassUtils.isAssignableValue(requiredType, newValue)) {
            if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) && newValue instanceof String) {
                TypeDescriptor elementTypeDesc = typeDescriptor.getElementTypeDescriptor();
                if (elementTypeDesc != null) {
                    Class<?> elementType = elementTypeDesc.getType();
                    if (Class.class == elementType || Enum.class.isAssignableFrom(elementType)) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String)newValue);
                    }
                }
            }

            if (editor == null) {
                editor = this.findDefaultEditor(requiredType);
            }

            convertedValue = this.doConvertValue(oldValue, convertedValue, requiredType, editor);
        }

        boolean standardConversion = false;
        if (requiredType != null) {
            if (convertedValue != null) {
                if (Object.class == requiredType) {
                    return (T)convertedValue;
                }

                if (requiredType.isArray()) {
                    if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String)convertedValue);
                    }

                    return (T)this.convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }

                if (convertedValue instanceof Collection) {
                    convertedValue = this.convertToTypedCollection((Collection)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                } else if (convertedValue instanceof Map) {
                    convertedValue = this.convertToTypedMap((Map)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }

                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                    standardConversion = true;
                }

                if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T)convertedValue.toString();
                }

                if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                    if (conversionAttemptEx == null && !requiredType.isInterface() && !requiredType.isEnum()) {
                        try {
                            Constructor<T> strCtor = requiredType.getConstructor(String.class);
                            return BeanUtils.instantiateClass(strCtor, new Object[]{convertedValue});
                        } catch (NoSuchMethodException var12) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No String constructor found on type [" + requiredType.getName() + "]", var12);
                            }
                        } catch (Exception var13) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Construction via String failed for type [" + requiredType.getName() + "]", var13);
                            }
                        }
                    }

                    String trimmedValue = ((String)convertedValue).trim();
                    if (requiredType.isEnum() && "".equals(trimmedValue)) {
                        return null;
                    }

                    convertedValue = this.attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                    standardConversion = true;
                } else if (convertedValue instanceof Number && Number.class.isAssignableFrom(requiredType)) {
                    //convertedValue = NumberUtils.convertNumberToTargetClass((Number)convertedValue, requiredType);
                    standardConversion = true;
                }
            } else if (javaUtilOptionalEmpty != null && requiredType == javaUtilOptionalEmpty.getClass()) {
                convertedValue = javaUtilOptionalEmpty;
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                if (conversionAttemptEx != null) {
                    throw conversionAttemptEx;
                }

                if (conversionService != null) {
                    TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
                    if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                        return (T)conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                    }
                }

                StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("' to required type '").append(ClassUtils.getQualifiedName(requiredType)).append("'");
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append("'");
                }

                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append("] returned inappropriate value of type '").append(ClassUtils.getDescriptiveType(convertedValue)).append("'");
                    throw new IllegalArgumentException(msg.toString());
                }

                msg.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(msg.toString());
            }
        }

        if (conversionAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && Object.class != requiredType) {
                throw conversionAttemptEx;
            }

            logger.debug("Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", conversionAttemptEx);
        }

        return (T)convertedValue;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        Object convertedValue = currentConvertedValue;
        if (Enum.class == requiredType) {
            int index = trimmedValue.lastIndexOf(46);
            if (index > -1) {
                String enumType = trimmedValue.substring(0, index);
                String fieldName = trimmedValue.substring(index + 1);
                ClassLoader cl = this.targetObject.getClass().getClassLoader();

                try {
                    Class<?> enumValueType = ClassUtils.forName(enumType, cl);
                    Field enumField = enumValueType.getField(fieldName);
                    convertedValue = enumField.get((Object)null);
                } catch (ClassNotFoundException var12) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Enum class [" + enumType + "] cannot be loaded", var12);
                    }
                } catch (Throwable var13) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", var13);
                    }
                }
            }
        }

        if (convertedValue == currentConvertedValue) {
            try {
                Field enumField = requiredType.getField(trimmedValue);
                ReflectionUtils.makeAccessible(enumField);
                convertedValue = enumField.get((Object)null);
            } catch (Throwable var11) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + convertedValue + "] isn't an enum value", var11);
                }
            }
        }

        return convertedValue;
    }

    private PropertyEditor findDefaultEditor(Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && String.class != requiredType) {
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }

        return editor;
    }

    private Object doConvertValue(Object oldValue, Object newValue, Class<?> requiredType, PropertyEditor editor) {
        Object convertedValue = newValue;
        Object returnValue;
        if (editor != null && !(newValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                returnValue = editor.getValue();
                if (returnValue != convertedValue) {
                    convertedValue = returnValue;
                    editor = null;
                }
            } catch (Exception var8) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", var8);
                }
            }
        }

        returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }

            convertedValue = StringUtils.arrayToCommaDelimitedString((String[])((String[])convertedValue));
        }

        if (convertedValue instanceof String) {
            if (editor != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }

                String newTextValue = (String)convertedValue;
                return this.doConvertTextValue(oldValue, newTextValue, editor);
            }

            if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }

        return returnValue;
    }

    private Object doConvertTextValue(Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        } catch (Exception var5) {
            if (logger.isDebugEnabled()) {
                logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", var5);
            }
        }

        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, String propertyName, Class<?> componentType) {
        Object result;
        int i;
        if (input instanceof Collection) {
            Collection<?> coll = (Collection)input;
            result = Array.newInstance(componentType, coll.size());
            i = 0;

            for(Iterator it = coll.iterator(); it.hasNext(); ++i) {
                Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), (Object)null, it.next(), componentType);
                Array.set(result, i, value);
            }

            return result;
        } else if (!input.getClass().isArray()) {
            Object newResult = Array.newInstance(componentType, 1);
            result = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, 0), (Object)null, input, componentType);
            Array.set(result, 0, result);
            return result;
        } else if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
            return input;
        } else {
            int arrayLength = Array.getLength(input);
            result = Array.newInstance(componentType, arrayLength);

            for(i = 0; i < arrayLength; ++i) {
                Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), (Object)null, Array.get(input, i), componentType);
                Array.set(result, i, value);
            }

            return result;
        }
    }

    private Collection<?> convertToTypedCollection(Collection<?> original, String propertyName, Class<?> requiredType, TypeDescriptor typeDescriptor) {
        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        } else {
            boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
            if (!approximable && !this.canCreateCopy(requiredType)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Custom Collection type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Collection as-is");
                }

                return original;
            } else {
                boolean originalAllowed = requiredType.isInstance(original);
                TypeDescriptor elementType = typeDescriptor.getElementTypeDescriptor();
                if (elementType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement((Class)null, propertyName)) {
                    return original;
                } else {
                    Iterator it;
                    try {
                        it = original.iterator();
                        if (it == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Collection of type [" + original.getClass().getName() + "] returned null Iterator - injecting original Collection as-is");
                            }

                            return original;
                        }
                    } catch (Throwable var17) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot access Collection of type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + var17);
                        }

                        return original;
                    }

                    Collection convertedCopy;
                    try {
                        if (approximable) {
                            convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
                        } else {
                            convertedCopy = (Collection)requiredType.newInstance();
                        }
                    } catch (Throwable var16) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + var16);
                        }

                        return original;
                    }

                    for(int i = 0; it.hasNext(); ++i) {
                        Object element = it.next();
                        String indexedPropertyName = this.buildIndexedPropertyName(propertyName, i);
                        Object convertedElement = this.convertIfNecessary(indexedPropertyName, (Object)null, element, elementType != null ? elementType.getType() : null, elementType);

                        try {
                            convertedCopy.add(convertedElement);
                        } catch (Throwable var15) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Collection type [" + original.getClass().getName() + "] seems to be read-only - injecting original Collection as-is: " + var15);
                            }

                            return original;
                        }

                        originalAllowed = originalAllowed && element == convertedElement;
                    }

                    return originalAllowed ? original : convertedCopy;
                }
            }
        }
    }

    private Map<?, ?> convertToTypedMap(Map<?, ?> original, String propertyName, Class<?> requiredType, TypeDescriptor typeDescriptor) {
        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        } else {
            boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
            if (!approximable && !this.canCreateCopy(requiredType)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Custom Map type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Map as-is");
                }

                return original;
            } else {
                boolean originalAllowed = requiredType.isInstance(original);
                TypeDescriptor keyType = typeDescriptor.getMapKeyTypeDescriptor();
                TypeDescriptor valueType = typeDescriptor.getMapValueTypeDescriptor();
                if (keyType == null && valueType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement((Class)null, propertyName)) {
                    return original;
                } else {
                    Iterator it;
                    try {
                        it = original.entrySet().iterator();
                        if (it == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Map of type [" + original.getClass().getName() + "] returned null Iterator - injecting original Map as-is");
                            }

                            return original;
                        }
                    } catch (Throwable var20) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot access Map of type [" + original.getClass().getName() + "] - injecting original Map as-is: " + var20);
                        }

                        return original;
                    }

                    Map convertedCopy;
                    try {
                        if (approximable) {
                            convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
                        } else {
                            convertedCopy = (Map)requiredType.newInstance();
                        }
                    } catch (Throwable var19) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot create copy of Map type [" + original.getClass().getName() + "] - injecting original Map as-is: " + var19);
                        }

                        return original;
                    }

                    Object key;
                    Object value;
                    Object convertedKey;
                    Object convertedValue;
                    for(; it.hasNext(); originalAllowed = originalAllowed && key == convertedKey && value == convertedValue) {
                        Map.Entry<?, ?> entry = (Map.Entry)it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        String keyedPropertyName = this.buildKeyedPropertyName(propertyName, key);
                        convertedKey = this.convertIfNecessary(keyedPropertyName, (Object)null, key, keyType != null ? keyType.getType() : null, keyType);
                        convertedValue = this.convertIfNecessary(keyedPropertyName, (Object)null, value, valueType != null ? valueType.getType() : null, valueType);

                        try {
                            convertedCopy.put(convertedKey, convertedValue);
                        } catch (Throwable var18) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Map type [" + original.getClass().getName() + "] seems to be read-only - injecting original Map as-is: " + var18);
                            }

                            return original;
                        }
                    }

                    return originalAllowed ? original : convertedCopy;
                }
            }
        }
    }

    private String buildIndexedPropertyName(String propertyName, int index) {
        return propertyName != null ? propertyName + "[" + index + "]" : null;
    }

    private String buildKeyedPropertyName(String propertyName, Object key) {
        return propertyName != null ? propertyName + "[" + key + "]" : null;
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, new Class[0]);
    }

    static {
        try {
            Class<?> clazz = ClassUtils.forName("java.util.Optional", TypeConverterDelegate.class.getClassLoader());
            javaUtilOptionalEmpty = ClassUtils.getMethod(clazz, "empty", new Class[0]).invoke((Object)null);
        } catch (Exception var1) {
        }

    }
}