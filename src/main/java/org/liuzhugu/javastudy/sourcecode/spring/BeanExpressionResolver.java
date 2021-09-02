package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;

public interface BeanExpressionResolver {
    Object evaluate(String var1, BeanExpressionContext var2) throws BeansException;
}
