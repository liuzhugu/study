package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.config.Scope;
import org.springframework.util.StringValueResolver;

public class EmbeddedValueResolver implements StringValueResolver {
    private final BeanExpressionContext exprContext;
    private final BeanExpressionResolver exprResolver;

    public EmbeddedValueResolver(ConfigurableBeanFactory beanFactory) {
        this.exprContext = new BeanExpressionContext(beanFactory, (Scope)null);
        this.exprResolver = beanFactory.getBeanExpressionResolver();
    }

    public String resolveStringValue(String strVal) {
        String value = this.exprContext.getBeanFactory().resolveEmbeddedValue(strVal);
        if (this.exprResolver != null && value != null) {
            Object evaluated = this.exprResolver.evaluate(value, this.exprContext);
            value = evaluated != null ? evaluated.toString() : null;
        }

        return value;
    }
}
