package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.BeanReference;

import java.util.ArrayList;
import java.util.List;

public class BeanComponentDefinition extends BeanDefinitionHolder implements ComponentDefinition {
    private BeanDefinition[] innerBeanDefinitions;
    private BeanReference[] beanReferences;

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName) {
        super(beanDefinition, beanName);
        this.findInnerBeanDefinitionsAndBeanReferences(beanDefinition);
    }

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName, String[] aliases) {
        super(beanDefinition, beanName, aliases);
        this.findInnerBeanDefinitionsAndBeanReferences(beanDefinition);
    }

    public BeanComponentDefinition(BeanDefinitionHolder holder) {
        super(holder);
        this.findInnerBeanDefinitionsAndBeanReferences(holder.getBeanDefinition());
    }

    private void findInnerBeanDefinitionsAndBeanReferences(BeanDefinition beanDefinition) {
        List<BeanDefinition> innerBeans = new ArrayList();
        List<BeanReference> references = new ArrayList();
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue[] var5 = propertyValues.getPropertyValues();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            PropertyValue propertyValue = var5[var7];
            Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                innerBeans.add(((BeanDefinitionHolder)value).getBeanDefinition());
            } else if (value instanceof BeanDefinition) {
                innerBeans.add((BeanDefinition)value);
            } else if (value instanceof BeanReference) {
                references.add((BeanReference)value);
            }
        }

        this.innerBeanDefinitions = (BeanDefinition[])innerBeans.toArray(new BeanDefinition[innerBeans.size()]);
        this.beanReferences = (BeanReference[])references.toArray(new BeanReference[references.size()]);
    }

    public String getName() {
        return this.getBeanName();
    }

    public String getDescription() {
        return this.getShortDescription();
    }

    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.getBeanDefinition()};
    }

    public BeanDefinition[] getInnerBeanDefinitions() {
        return this.innerBeanDefinitions;
    }

    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    public String toString() {
        return this.getDescription();
    }

    public boolean equals(Object other) {
        return this == other || other instanceof BeanComponentDefinition && super.equals(other);
    }
}