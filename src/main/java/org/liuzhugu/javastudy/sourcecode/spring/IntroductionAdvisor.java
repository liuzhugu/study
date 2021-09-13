package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionInfo;

public interface IntroductionAdvisor extends Advisor, IntroductionInfo {
    ClassFilter getClassFilter();

    void validateInterfaces() throws IllegalArgumentException;
}
