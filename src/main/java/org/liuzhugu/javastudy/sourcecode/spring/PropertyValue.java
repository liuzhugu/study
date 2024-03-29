package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable {
    private final String name;
    private final Object value;
    private boolean optional = false;
    private boolean converted = false;
    private Object convertedValue;
    volatile Boolean conversionNecessary;
    transient volatile Object resolvedTokens;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public PropertyValue(PropertyValue original) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
        this.optional = original.isOptional();
        this.converted = original.converted;
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.setSource(original.getSource());
        this.copyAttributesFrom(original);
    }

    public PropertyValue(PropertyValue original, Object newValue) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.setSource(original);
        this.copyAttributesFrom(original);
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original = this;

        for(Object source = this.getSource(); source instanceof PropertyValue && source != original; source = original.getSource()) {
            original = (PropertyValue)source;
        }

        return original;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public synchronized boolean isConverted() {
        return this.converted;
    }

    public synchronized void setConvertedValue(Object value) {
        this.converted = true;
        this.convertedValue = value;
    }

    public synchronized Object getConvertedValue() {
        return this.convertedValue;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof PropertyValue)) {
            return false;
        } else {
            PropertyValue otherPv = (PropertyValue)other;
            return this.name.equals(otherPv.name) && ObjectUtils.nullSafeEquals(this.value, otherPv.value) && ObjectUtils.nullSafeEquals(this.getSource(), otherPv.getSource());
        }
    }

    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return "bean property '" + this.name + "'";
    }
}
