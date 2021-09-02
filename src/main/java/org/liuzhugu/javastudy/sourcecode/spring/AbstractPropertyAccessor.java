package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.*;

import java.util.*;

public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor {
    private boolean extractOldValueForEditor = false;
    private boolean autoGrowNestedPaths = false;

    public AbstractPropertyAccessor() {
    }

    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }

    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }

    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void setPropertyValue(PropertyValue pv) throws BeansException {
        this.setPropertyValue(pv.getName(), pv.getValue());
    }

    public void setPropertyValues(Map<?, ?> map) throws BeansException {
        this.setPropertyValues((PropertyValues)(new MutablePropertyValues(map)));
    }

    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        this.setPropertyValues(pvs, false, false);
    }

    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
        this.setPropertyValues(pvs, ignoreUnknown, false);
    }

    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
        List<PropertyAccessException> propertyAccessExceptions = null;
        List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues)pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        Iterator var6 = propertyValues.iterator();

        while(var6.hasNext()) {
            PropertyValue pv = (PropertyValue)var6.next();

            try {
                this.setPropertyValue(pv);
            } catch (NotWritablePropertyException var9) {
                if (!ignoreUnknown) {
                    throw var9;
                }
            } catch (NullValueInNestedPathException var10) {
                if (!ignoreInvalid) {
                    throw var10;
                }
            } catch (PropertyAccessException var11) {
                if (propertyAccessExceptions == null) {
                    propertyAccessExceptions = new LinkedList();
                }

                propertyAccessExceptions.add(var11);
            }
        }

        if (propertyAccessExceptions != null) {
            PropertyAccessException[] paeArray = (PropertyAccessException[])propertyAccessExceptions.toArray(new PropertyAccessException[propertyAccessExceptions.size()]);
            throw new PropertyBatchUpdateException(paeArray);
        }
    }

    public Class<?> getPropertyType(String propertyPath) {
        return null;
    }

    public abstract Object getPropertyValue(String var1) throws BeansException;

    public abstract void setPropertyValue(String var1, Object var2) throws BeansException;
}