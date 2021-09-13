package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.TargetClassAware;

public interface TargetSource extends TargetClassAware {
    Class<?> getTargetClass();

    boolean isStatic();

    Object getTarget() throws Exception;

    void releaseTarget(Object var1) throws Exception;
}