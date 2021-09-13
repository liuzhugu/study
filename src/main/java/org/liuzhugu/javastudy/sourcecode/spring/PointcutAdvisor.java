package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.Pointcut;

public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}