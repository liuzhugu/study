package org.liuzhugu.javastudy.sourcecode.spring;

import org.w3c.dom.Node;

public interface BeanDefinitionDecorator {
    BeanDefinitionHolder decorate(Node var1, BeanDefinitionHolder var2, ParserContext var3);
}