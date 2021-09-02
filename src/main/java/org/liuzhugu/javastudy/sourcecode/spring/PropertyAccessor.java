package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;

public interface PropertyAccessor {
    String NESTED_PROPERTY_SEPARATOR = ".";
    char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
    String PROPERTY_KEY_PREFIX = "[";
    char PROPERTY_KEY_PREFIX_CHAR = '[';
    String PROPERTY_KEY_SUFFIX = "]";
    char PROPERTY_KEY_SUFFIX_CHAR = ']';

    boolean isReadableProperty(String var1);

    boolean isWritableProperty(String var1);

    Class<?> getPropertyType(String var1) throws BeansException;

    TypeDescriptor getPropertyTypeDescriptor(String var1) throws BeansException;

    Object getPropertyValue(String var1) throws BeansException;

    void setPropertyValue(String var1, Object var2) throws BeansException;

    void setPropertyValue(PropertyValue var1) throws BeansException;

    void setPropertyValues(Map<?, ?> var1) throws BeansException;

    void setPropertyValues(PropertyValues var1) throws BeansException;

    void setPropertyValues(PropertyValues var1, boolean var2) throws BeansException;

    void setPropertyValues(PropertyValues var1, boolean var2, boolean var3) throws BeansException;
}
