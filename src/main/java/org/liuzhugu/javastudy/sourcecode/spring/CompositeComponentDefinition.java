package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public class CompositeComponentDefinition extends AbstractComponentDefinition {
    private final String name;
    private final Object source;
    private final List<ComponentDefinition> nestedComponents = new LinkedList();

    public CompositeComponentDefinition(String name, Object source) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return this.name;
    }

    public Object getSource() {
        return this.source;
    }

    public void addNestedComponent(ComponentDefinition component) {
        Assert.notNull(component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }

    public ComponentDefinition[] getNestedComponents() {
        return (ComponentDefinition[])this.nestedComponents.toArray(new ComponentDefinition[this.nestedComponents.size()]);
    }
}