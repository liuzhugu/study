package org.liuzhugu.javastudy.sourcecode.spring;

public abstract class AbstractGenericPointcutAdvisor extends AbstractPointcutAdvisor {
    private Advice advice;

    public AbstractGenericPointcutAdvisor() {
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Advice getAdvice() {
        return this.advice;
    }

    public String toString() {
        return this.getClass().getName() + ": advice [" + this.getAdvice() + "]";
    }
}
