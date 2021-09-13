package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionInfo;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable {
    private final Advice advice;
    private final Set<Class<?>> interfaces;
    private int order;

    public DefaultIntroductionAdvisor(Advice advice) {
        this(advice, advice instanceof IntroductionInfo ? (IntroductionInfo)advice : null);
    }

    public DefaultIntroductionAdvisor(Advice advice, IntroductionInfo introductionInfo) {
        this.interfaces = new LinkedHashSet();
        this.order = 2147483647;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
            }

            Class[] var4 = introducedInterfaces;
            int var5 = introducedInterfaces.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Class<?> ifc = var4[var6];
                this.addInterface(ifc);
            }
        }

    }

    public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class<?> intf) {
        this.interfaces = new LinkedHashSet();
        this.order = 2147483647;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.addInterface(intf);
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
        } else {
            this.interfaces.add(intf);
        }
    }

    public Class<?>[] getInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    public void validateInterfaces() throws IllegalArgumentException {
        Iterator var1 = this.interfaces.iterator();

        Class ifc;
        do {
            if (!var1.hasNext()) {
                return;
            }

            ifc = (Class)var1.next();
        } while(!(this.advice instanceof DynamicIntroductionAdvice) || ((DynamicIntroductionAdvice)this.advice).implementsInterface(ifc));

        throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] does not implement interface [" + ifc.getName() + "] specified for introduction");
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public Advice getAdvice() {
        return this.advice;
    }

    public boolean isPerInstance() {
        return true;
    }

    public ClassFilter getClassFilter() {
        return this;
    }

    public boolean matches(Class<?> clazz) {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof DefaultIntroductionAdvisor)) {
            return false;
        } else {
            DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor)other;
            return this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces);
        }
    }

    public int hashCode() {
        return this.advice.hashCode() * 13 + this.interfaces.hashCode();
    }

    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + ": advice [" + this.advice + "]; interfaces " + ClassUtils.classNamesToString(this.interfaces);
    }
}
