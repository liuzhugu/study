package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.util.Assert;

public class PointcutComponentDefinition extends AbstractComponentDefinition {
    private final String pointcutBeanName;
    private final BeanDefinition pointcutDefinition;
    private final String description;

    public PointcutComponentDefinition(String pointcutBeanName, BeanDefinition pointcutDefinition, String expression) {
        Assert.notNull(pointcutBeanName, "Bean name must not be null");
        Assert.notNull(pointcutDefinition, "Pointcut definition must not be null");
        Assert.notNull(expression, "Expression must not be null");
        this.pointcutBeanName = pointcutBeanName;
        this.pointcutDefinition = pointcutDefinition;
        this.description = "Pointcut <name='" + pointcutBeanName + "', expression=[" + expression + "]>";
    }

    public String getName() {
        return this.pointcutBeanName;
    }

    public String getDescription() {
        return this.description;
    }

    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.pointcutDefinition};
    }

    public Object getSource() {
        return this.pointcutDefinition.getSource();
    }
}