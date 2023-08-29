package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.BeanReference;
import org.springframework.util.Assert;

public class AdvisorComponentDefinition extends AbstractComponentDefinition {
    private final String advisorBeanName;
    private final BeanDefinition advisorDefinition;
    private String description;
    private BeanReference[] beanReferences;
    private BeanDefinition[] beanDefinitions;

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition) {
        this(advisorBeanName, advisorDefinition, (BeanDefinition)null);
    }

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {
        Assert.notNull(advisorBeanName, "'advisorBeanName' must not be null");
        Assert.notNull(advisorDefinition, "'advisorDefinition' must not be null");
        this.advisorBeanName = advisorBeanName;
        this.advisorDefinition = advisorDefinition;
        this.unwrapDefinitions(advisorDefinition, pointcutDefinition);
    }

    private void unwrapDefinitions(BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {
        MutablePropertyValues pvs = advisorDefinition.getPropertyValues();
        BeanReference adviceReference = (BeanReference)pvs.getPropertyValue("adviceBeanName").getValue();
        if (pointcutDefinition != null) {
            this.beanReferences = new BeanReference[]{adviceReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition, pointcutDefinition};
            this.description = this.buildDescription(adviceReference, pointcutDefinition);
        } else {
            BeanReference pointcutReference = (BeanReference)pvs.getPropertyValue("pointcut").getValue();
            this.beanReferences = new BeanReference[]{adviceReference, pointcutReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition};
            this.description = this.buildDescription(adviceReference, pointcutReference);
        }

    }

    private String buildDescription(BeanReference adviceReference, BeanDefinition pointcutDefinition) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(expression)=[" + pointcutDefinition.getPropertyValues().getPropertyValue("expression").getValue() + "]>";
    }

    private String buildDescription(BeanReference adviceReference, BeanReference pointcutReference) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(ref)='" + pointcutReference.getBeanName() + "'>";
    }

    public String getName() {
        return this.advisorBeanName;
    }

    public String getDescription() {
        return this.description;
    }

    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    public Object getSource() {
        return this.advisorDefinition.getSource();
    }
}
