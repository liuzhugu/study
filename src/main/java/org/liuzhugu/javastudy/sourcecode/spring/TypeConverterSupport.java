package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;

import java.lang.reflect.Field;

public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {
    TypeConverterDelegate typeConverterDelegate;

    public TypeConverterSupport() {
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
        return this.doConvert(value, requiredType, (MethodParameter)null, (Field)null);
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {
        return this.doConvert(value, requiredType, methodParam, (Field)null);
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) throws TypeMismatchException {
        return this.doConvert(value, requiredType, (MethodParameter)null, field);
    }

    private <T> T doConvert(Object value, Class<T> requiredType, MethodParameter methodParam, Field field) throws TypeMismatchException {
        try {
            return field != null ? this.typeConverterDelegate.convertIfNecessary(value, requiredType, field) : this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
        } catch (ConverterNotFoundException var6) {
            throw new ConversionNotSupportedException(value, requiredType, var6);
        } catch (ConversionException var7) {
            throw new TypeMismatchException(value, requiredType, var7);
        } catch (IllegalStateException var8) {
            throw new ConversionNotSupportedException(value, requiredType, var8);
        } catch (IllegalArgumentException var9) {
            throw new TypeMismatchException(value, requiredType, var9);
        }
    }
}
