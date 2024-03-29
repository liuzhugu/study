package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.Pointcut;

import java.io.Serializable;

public class DefaultPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable {
    private Pointcut pointcut;

    public DefaultPointcutAdvisor() {
        this.pointcut = Pointcut.TRUE;
    }

    public DefaultPointcutAdvisor(Advice advice) {
        this(Pointcut.TRUE, advice);
    }

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = Pointcut.TRUE;
        this.pointcut = pointcut;
        this.setAdvice(advice);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut != null ? pointcut : Pointcut.TRUE;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public String toString() {
        return this.getClass().getName() + ": pointcut [" + this.getPointcut() + "]; advice [" + this.getAdvice() + "]";
    }
}
