package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered, Serializable {
    private Integer order;

    public AbstractPointcutAdvisor() {
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        if (this.order != null) {
            return this.order;
        } else {
            Advice advice = this.getAdvice();
            return advice instanceof Ordered ? ((Ordered)advice).getOrder() : 2147483647;
        }
    }

    public boolean isPerInstance() {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof PointcutAdvisor)) {
            return false;
        } else {
            PointcutAdvisor otherAdvisor = (PointcutAdvisor)other;
            return ObjectUtils.nullSafeEquals(this.getAdvice(), otherAdvisor.getAdvice()) && ObjectUtils.nullSafeEquals(this.getPointcut(), otherAdvisor.getPointcut());
        }
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}