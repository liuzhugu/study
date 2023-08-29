package org.liuzhugu.javastudy.sourcecode.spring;


import java.util.Stack;

public final class ParserContext {
    private final XmlReaderContext readerContext;
    private final BeanDefinitionParserDelegate delegate;
    private BeanDefinition containingBeanDefinition;
    private final Stack<CompositeComponentDefinition> containingComponents = new Stack();

    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
        this.readerContext = readerContext;
        this.delegate = delegate;
    }

    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate, BeanDefinition containingBeanDefinition) {
        this.readerContext = readerContext;
        this.delegate = delegate;
        this.containingBeanDefinition = containingBeanDefinition;
    }

    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.readerContext.getRegistry();
    }

    public final BeanDefinitionParserDelegate getDelegate() {
        return this.delegate;
    }

    public final BeanDefinition getContainingBeanDefinition() {
        return this.containingBeanDefinition;
    }

    public final boolean isNested() {
        return this.containingBeanDefinition != null;
    }

    public boolean isDefaultLazyInit() {
        return "true".equals(this.delegate.getDefaults().getLazyInit());
    }

    public Object extractSource(Object sourceCandidate) {
        return this.readerContext.extractSource(sourceCandidate);
    }

    public CompositeComponentDefinition getContainingComponent() {
        return !this.containingComponents.isEmpty() ? (CompositeComponentDefinition)this.containingComponents.lastElement() : null;
    }

    public void pushContainingComponent(CompositeComponentDefinition containingComponent) {
        this.containingComponents.push(containingComponent);
    }

    public CompositeComponentDefinition popContainingComponent() {
        return (CompositeComponentDefinition)this.containingComponents.pop();
    }

    public void popAndRegisterContainingComponent() {
        this.registerComponent(this.popContainingComponent());
    }

    public void registerComponent(ComponentDefinition component) {
        CompositeComponentDefinition containingComponent = this.getContainingComponent();
        if (containingComponent != null) {
            containingComponent.addNestedComponent(component);
        } else {
            this.readerContext.fireComponentRegistered(component);
        }

    }

    public void registerBeanComponent(BeanComponentDefinition component) {
        BeanDefinitionReaderUtils.registerBeanDefinition(component, this.getRegistry());
        this.registerComponent(component);
    }
}
