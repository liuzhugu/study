package org.liuzhugu.javastudy.sourcecode.spring;


public interface PropertyValues {
    PropertyValue[] getPropertyValues();

    PropertyValue getPropertyValue(String var1);

    PropertyValues changesSince(PropertyValues var1);

    boolean contains(String var1);

    boolean isEmpty();
}
