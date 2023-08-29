package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.support.ManagedList;
import org.springframework.util.Assert;

public class ManagedArray extends ManagedList<Object> {
    volatile Class<?> resolvedElementType;

    public ManagedArray(String elementTypeName, int size) {
        super(size);
        Assert.notNull(elementTypeName, "elementTypeName must not be null");
        this.setElementTypeName(elementTypeName);
    }
}