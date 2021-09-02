package org.liuzhugu.javastudy.sourcecode.spring;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class MethodOverrides {
    private final Set<MethodOverride> overrides = Collections.synchronizedSet(new LinkedHashSet(0));
    private volatile boolean modified = false;

    public MethodOverrides() {
    }

    public MethodOverrides(MethodOverrides other) {
        this.addOverrides(other);
    }

    public void addOverrides(MethodOverrides other) {
        if (other != null) {
            this.modified = true;
            this.overrides.addAll(other.overrides);
        }

    }

    public void addOverride(MethodOverride override) {
        this.modified = true;
        this.overrides.add(override);
    }

    public Set<MethodOverride> getOverrides() {
        this.modified = true;
        return this.overrides;
    }

    public boolean isEmpty() {
        return !this.modified || this.overrides.isEmpty();
    }

    public MethodOverride getOverride(Method method) {
        if (!this.modified) {
            return null;
        } else {
            synchronized(this.overrides) {
                MethodOverride match = null;
                Iterator var4 = this.overrides.iterator();

                while(var4.hasNext()) {
                    MethodOverride candidate = (MethodOverride)var4.next();
                    if (candidate.matches(method)) {
                        match = candidate;
                    }
                }

                return match;
            }
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof MethodOverrides)) {
            return false;
        } else {
            MethodOverrides that = (MethodOverrides)other;
            return this.overrides.equals(that.overrides);
        }
    }

    public int hashCode() {
        return this.overrides.hashCode();
    }
}

