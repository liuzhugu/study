package org.liuzhugu.javastudy.sourcecode.spring;

public abstract class GlobalAdvisorAdapterRegistry {
    private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

    public GlobalAdvisorAdapterRegistry() {
    }

    public static AdvisorAdapterRegistry getInstance() {
        return instance;
    }

    static void reset() {
        instance = new DefaultAdvisorAdapterRegistry();
    }
}
