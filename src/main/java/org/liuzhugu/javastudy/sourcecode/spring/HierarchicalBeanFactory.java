package org.liuzhugu.javastudy.sourcecode.spring;


public interface HierarchicalBeanFactory extends BeanFactory {
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String var1);
}
