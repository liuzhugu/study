package org.liuzhugu.javastudy.sourcecode.spring;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class NamespaceHandlerSupport implements NamespaceHandler {
    private final Map<String, BeanDefinitionParser> parsers = new HashMap();
    private final Map<String, BeanDefinitionDecorator> decorators = new HashMap();
    private final Map<String, BeanDefinitionDecorator> attributeDecorators = new HashMap();

    public NamespaceHandlerSupport() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return this.findParserForElement(element, parserContext).parse(element, parserContext);
    }

    private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
        String localName = parserContext.getDelegate().getLocalName(element);
        BeanDefinitionParser parser = (BeanDefinitionParser)this.parsers.get(localName);
        if (parser == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
        }

        return parser;
    }

    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        return this.findDecoratorForNode(node, parserContext).decorate(node, definition, parserContext);
    }

    private BeanDefinitionDecorator findDecoratorForNode(Node node, ParserContext parserContext) {
        BeanDefinitionDecorator decorator = null;
        String localName = parserContext.getDelegate().getLocalName(node);
        if (node instanceof Element) {
            decorator = (BeanDefinitionDecorator)this.decorators.get(localName);
        } else if (node instanceof Attr) {
            decorator = (BeanDefinitionDecorator)this.attributeDecorators.get(localName);
        } else {
            parserContext.getReaderContext().fatal("Cannot decorate based on Nodes of type [" + node.getClass().getName() + "]", node);
        }

        if (decorator == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionDecorator for " + (node instanceof Element ? "element" : "attribute") + " [" + localName + "]", node);
        }

        return decorator;
    }

    protected final void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser) {
        this.parsers.put(elementName, parser);
    }

    protected final void registerBeanDefinitionDecorator(String elementName, BeanDefinitionDecorator dec) {
        this.decorators.put(elementName, dec);
    }

    protected final void registerBeanDefinitionDecoratorForAttribute(String attrName, BeanDefinitionDecorator dec) {
        this.attributeDecorators.put(attrName, dec);
    }
}