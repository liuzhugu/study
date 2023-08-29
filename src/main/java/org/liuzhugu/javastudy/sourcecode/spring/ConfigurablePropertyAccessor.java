package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.core.convert.ConversionService;

public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {
    void setConversionService(ConversionService var1);

    ConversionService getConversionService();

    void setExtractOldValueForEditor(boolean var1);

    boolean isExtractOldValueForEditor();

    void setAutoGrowNestedPaths(boolean var1);

    boolean isAutoGrowNestedPaths();
}