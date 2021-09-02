package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext implements BeanNameAware, InitializingBean {
    private String[] configLocations;
    private boolean setIdCalled = false;

    public AbstractRefreshableConfigApplicationContext() {
    }

    public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public void setConfigLocation(String location) {
        this.setConfigLocations(StringUtils.tokenizeToStringArray(location, ",; \t\n"));
    }

    public void setConfigLocations(String... locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];

            for(int i = 0; i < locations.length; ++i) {
                this.configLocations[i] = this.resolvePath(locations[i]).trim();
            }
        } else {
            this.configLocations = null;
        }

    }

    protected String[] getConfigLocations() {
        return this.configLocations != null ? this.configLocations : this.getDefaultConfigLocations();
    }

    protected String[] getDefaultConfigLocations() {
        return null;
    }

    protected String resolvePath(String path) {
        return this.getEnvironment().resolveRequiredPlaceholders(path);
    }

    public void setId(String id) {
        super.setId(id);
        this.setIdCalled = true;
    }

    public void setBeanName(String name) {
        if (!this.setIdCalled) {
            super.setId(name);
            this.setDisplayName("ApplicationContext '" + name + "'");
        }

    }

    public void afterPropertiesSet() {
        if (!this.isActive()) {
            this.refresh();
        }

    }
}
