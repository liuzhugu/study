package org.liuzhugu.javastudy.sourcecode.spring;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface NamespaceHandler {
    void init();

    BeanDefinition parse(Element var1, ParserContext var2);

    BeanDefinitionHolder decorate(Node var1, BeanDefinitionHolder var2, ParserContext var3);
}