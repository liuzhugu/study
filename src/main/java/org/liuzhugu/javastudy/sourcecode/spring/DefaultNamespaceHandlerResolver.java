package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver {
    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";
    protected final Log logger;
    private final ClassLoader classLoader;
    private final String handlerMappingsLocation;
    private volatile Map<String, Object> handlerMappings;

    public DefaultNamespaceHandlerResolver() {
        this((ClassLoader)null, "META-INF/spring.handlers");
    }

    public DefaultNamespaceHandlerResolver(ClassLoader classLoader) {
        this(classLoader, "META-INF/spring.handlers");
    }

    public DefaultNamespaceHandlerResolver(ClassLoader classLoader, String handlerMappingsLocation) {
        this.logger = LogFactory.getLog(this.getClass());
        Assert.notNull(handlerMappingsLocation, "Handler mappings location must not be null");
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        this.handlerMappingsLocation = handlerMappingsLocation;
    }

    public NamespaceHandler resolve(String namespaceUri) {
        Map<String, Object> handlerMappings = this.getHandlerMappings();
        Object handlerOrClassName = handlerMappings.get(namespaceUri);
        if (handlerOrClassName == null) {
            return null;
        } else if (handlerOrClassName instanceof NamespaceHandler) {
            return (NamespaceHandler)handlerOrClassName;
        } else {
            String className = (String)handlerOrClassName;

            try {
                Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
                if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                    throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri + "] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
                } else {
                    NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
                    namespaceHandler.init();
                    handlerMappings.put(namespaceUri, namespaceHandler);
                    return namespaceHandler;
                }
            } catch (ClassNotFoundException var7) {
                throw new FatalBeanException("NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "] not found", var7);
            } catch (LinkageError var8) {
                throw new FatalBeanException("Invalid NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "]: problem with handler class file or dependent class", var8);
            }
        }
    }

    private Map<String, Object> getHandlerMappings() {
        if (this.handlerMappings == null) {
            synchronized(this) {
                if (this.handlerMappings == null) {
                    try {
                        Properties mappings = PropertiesLoaderUtils.loadAllProperties(this.handlerMappingsLocation, this.classLoader);

                        Map<String, Object> handlerMappings = new ConcurrentHashMap(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, handlerMappings);
                        this.handlerMappings = handlerMappings;
                    } catch (IOException var5) {
                        throw new IllegalStateException("Unable to load NamespaceHandler mappings from location [" + this.handlerMappingsLocation + "]", var5);
                    }
                }
            }
        }

        this.handlerMappings.put("http://www.springframework.org/schema/aop","org.liuzhugu.javastudy.sourcecode.spring.AopNamespaceHandler");
        return this.handlerMappings;
    }

    public String toString() {
        return "NamespaceHandlerResolver using mappings " + this.getHandlerMappings();
    }
}
