package org.liuzhugu.javastudy.sourcecode.spring;


public class GenericBeanDefinition extends AbstractBeanDefinition {
    private String parentName;

    public GenericBeanDefinition() {
    }

    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return this.parentName;
    }

    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

    public boolean equals(Object other) {
        return this == other || other instanceof GenericBeanDefinition && super.equals(other);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Generic bean");
        if (this.parentName != null) {
            sb.append(" with parent '").append(this.parentName).append("'");
        }

        sb.append(": ").append(super.toString());
        return sb.toString();
    }
}