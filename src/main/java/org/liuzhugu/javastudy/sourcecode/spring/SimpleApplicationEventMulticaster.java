package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ErrorHandler;

import java.util.Iterator;
import java.util.concurrent.Executor;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    private Executor taskExecutor;
    private ErrorHandler errorHandler;

    public SimpleApplicationEventMulticaster() {
    }

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        this.setBeanFactory(beanFactory);
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void multicastEvent(ApplicationEvent event) {
        this.multicastEvent(event, this.resolveDefaultEventType(event));
    }

    public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
        Iterator var4 = this.getApplicationListeners(event, type).iterator();

        while(var4.hasNext()) {
            final ApplicationListener<?> listener = (ApplicationListener)var4.next();
            Executor executor = this.getTaskExecutor();
            if (executor != null) {
                executor.execute(new Runnable() {
                    public void run() {
                        SimpleApplicationEventMulticaster.this.invokeListener(listener, event);
                    }
                });
            } else {
                this.invokeListener(listener, event);
            }
        }

    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }

    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler != null) {
            try {
                this.doInvokeListener(listener, event);
            } catch (Throwable var5) {
                errorHandler.handleError(var5);
            }
        } else {
            this.doInvokeListener(listener, event);
        }

    }

    private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        } catch (ClassCastException var6) {
            String msg = var6.getMessage();
            if (msg != null && !this.matchesClassCastMessage(msg, event.getClass().getName())) {
                throw var6;
            }

            Log logger = LogFactory.getLog(this.getClass());
            if (logger.isDebugEnabled()) {
                logger.debug("Non-matching event type for listener: " + listener, var6);
            }
        }

    }

    private boolean matchesClassCastMessage(String classCastMessage, String eventClassName) {
        if (classCastMessage.startsWith(eventClassName)) {
            return true;
        } else {
            int moduleSeparatorIndex = classCastMessage.indexOf(47);
            return moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClassName, moduleSeparatorIndex + 1);
        }
    }
}
