package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.context.expression.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardBeanExpressionResolver implements BeanExpressionResolver {
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";
    private String expressionPrefix = "#{";
    private String expressionSuffix = "}";
    private ExpressionParser expressionParser;
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap(256);
    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache = new ConcurrentHashMap(8);
    private final ParserContext beanExpressionParserContext;

    public StandardBeanExpressionResolver() {
        this.beanExpressionParserContext = new NamelessClass_1();
        this.expressionParser = new SpelExpressionParser();
    }

    class NamelessClass_1 implements ParserContext {
        NamelessClass_1() {
        }

        public boolean isTemplate() {
            return true;
        }

        public String getExpressionPrefix() {
            return StandardBeanExpressionResolver.this.expressionPrefix;
        }

        public String getExpressionSuffix() {
            return StandardBeanExpressionResolver.this.expressionSuffix;
        }
    }
    public StandardBeanExpressionResolver(ClassLoader beanClassLoader) {

        this.beanExpressionParserContext = new NamelessClass_1();
        this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration((SpelCompilerMode)null, beanClassLoader));
    }

    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }

    public Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException {
        if (!StringUtils.hasLength(value)) {
            return value;
        } else {
            try {
                Expression expr = (Expression)this.expressionCache.get(value);
                if (expr == null) {
                    expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                    this.expressionCache.put(value, expr);
                }

                StandardEvaluationContext sec = (StandardEvaluationContext)this.evaluationCache.get(evalContext);
                if (sec == null) {
                    sec = new StandardEvaluationContext(evalContext);
                    sec.addPropertyAccessor(new BeanExpressionContextAccessor());
                    sec.addPropertyAccessor(new BeanFactoryAccessor());
                    sec.addPropertyAccessor(new MapAccessor());
                    sec.addPropertyAccessor(new EnvironmentAccessor());
                    sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
                    sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
                    ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
                    if (conversionService != null) {
                        sec.setTypeConverter(new StandardTypeConverter(conversionService));
                    }

                    this.customizeEvaluationContext(sec);
                    this.evaluationCache.put(evalContext, sec);
                }

                return expr.getValue(sec);
            } catch (Throwable var6) {
                throw new BeanExpressionException("Expression parsing failed", var6);
            }
        }
    }

    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
    }
}
