package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {
    private final Log logger = LogFactory.getLog(this.getClass());
    private volatile long timeoutPerShutdownPhase = 30000L;
    private volatile boolean running;
    private volatile ConfigurableListableBeanFactory beanFactory;

    public DefaultLifecycleProcessor() {
    }

    public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        } else {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }

    public void start() {
        this.startBeans(false);
        this.running = true;
    }

    public void stop() {
        this.stopBeans();
        this.running = false;
    }

    public void onRefresh() {
        this.startBeans(true);
        this.running = true;
    }

    public void onClose() {
        this.stopBeans();
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    private void startBeans(boolean autoStartupOnly) {
        Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        Map<Integer, DefaultLifecycleProcessor.LifecycleGroup> phases = new HashMap();
        Iterator var4 = lifecycleBeans.entrySet().iterator();

        while(true) {
            Map.Entry entry;
            Lifecycle bean;
            do {
                if (!var4.hasNext()) {
                    if (!phases.isEmpty()) {
                        List<Integer> keys = new ArrayList(phases.keySet());
                        Collections.sort(keys);
                        Iterator var10 = keys.iterator();

                        while(var10.hasNext()) {
                            Integer key = (Integer)var10.next();
                            ((DefaultLifecycleProcessor.LifecycleGroup)phases.get(key)).start();
                        }
                    }

                    return;
                }

                entry = (Map.Entry)var4.next();
                bean = (Lifecycle)entry.getValue();
            } while(autoStartupOnly && (!(bean instanceof SmartLifecycle) || !((SmartLifecycle)bean).isAutoStartup()));

            int phase = this.getPhase(bean);
            DefaultLifecycleProcessor.LifecycleGroup group = (DefaultLifecycleProcessor.LifecycleGroup)phases.get(phase);
            if (group == null) {
                group = new DefaultLifecycleProcessor.LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
                phases.put(phase, group);
            }

            group.add((String)entry.getKey(), bean);
        }
    }

    private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
        Lifecycle bean = (Lifecycle)lifecycleBeans.remove(beanName);
        if (bean != null && bean != this) {
            String[] dependenciesForBean = this.beanFactory.getDependenciesForBean(beanName);
            String[] var6 = dependenciesForBean;
            int var7 = dependenciesForBean.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String dependency = var6[var8];
                this.doStart(lifecycleBeans, dependency, autoStartupOnly);
            }

            if (!bean.isRunning() && (!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle)bean).isAutoStartup())) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Starting bean '" + beanName + "' of type [" + bean.getClass() + "]");
                }

                try {
                    bean.start();
                } catch (Throwable var10) {
                    throw new ApplicationContextException("Failed to start bean '" + beanName + "'", var10);
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Successfully started bean '" + beanName + "'");
                }
            }
        }

    }

    private void stopBeans() {
        Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        Map<Integer, DefaultLifecycleProcessor.LifecycleGroup> phases = new HashMap();

        Map.Entry entry;
        Lifecycle bean;
        DefaultLifecycleProcessor.LifecycleGroup group;
        for(Iterator var3 = lifecycleBeans.entrySet().iterator(); var3.hasNext(); group.add((String)entry.getKey(), bean)) {
            entry = (Map.Entry)var3.next();
            bean = (Lifecycle)entry.getValue();
            int shutdownOrder = this.getPhase(bean);
            group = (DefaultLifecycleProcessor.LifecycleGroup)phases.get(shutdownOrder);
            if (group == null) {
                group = new DefaultLifecycleProcessor.LifecycleGroup(shutdownOrder, this.timeoutPerShutdownPhase, lifecycleBeans, false);
                phases.put(shutdownOrder, group);
            }
        }

        if (!phases.isEmpty()) {
            List<Integer> keys = new ArrayList(phases.keySet());
            Collections.sort(keys, Collections.reverseOrder());
            Iterator var9 = keys.iterator();

            while(var9.hasNext()) {
                Integer key = (Integer)var9.next();
                ((DefaultLifecycleProcessor.LifecycleGroup)phases.get(key)).stop();
            }
        }

    }

    private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName, final CountDownLatch latch, final Set<String> countDownBeanNames) {
        Lifecycle bean = (Lifecycle)lifecycleBeans.remove(beanName);
        if (bean != null) {
            String[] dependentBeans = this.beanFactory.getDependentBeans(beanName);
            String[] var7 = dependentBeans;
            int var8 = dependentBeans.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String dependentBean = var7[var9];
                this.doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
            }

            try {
                if (bean.isRunning()) {
                    if (bean instanceof SmartLifecycle) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Asking bean '" + beanName + "' of type [" + bean.getClass() + "] to stop");
                        }

                        countDownBeanNames.add(beanName);
                        ((SmartLifecycle)bean).stop(new Runnable() {
                            public void run() {
                                latch.countDown();
                                countDownBeanNames.remove(beanName);
                                if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                                    DefaultLifecycleProcessor.this.logger.debug("Bean '" + beanName + "' completed its stop procedure");
                                }

                            }
                        });
                    } else {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Stopping bean '" + beanName + "' of type [" + bean.getClass() + "]");
                        }

                        bean.stop();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Successfully stopped bean '" + beanName + "'");
                        }
                    }
                } else if (bean instanceof SmartLifecycle) {
                    latch.countDown();
                }
            } catch (Throwable var11) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Failed to stop bean '" + beanName + "'", var11);
                }
            }
        }

    }

    protected Map<String, Lifecycle> getLifecycleBeans() {
        Map<String, Lifecycle> beans = new LinkedHashMap();
        String[] beanNames = this.beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
        String[] var3 = beanNames;
        int var4 = beanNames.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String beanName = var3[var5];
            String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
            boolean isFactoryBean = this.beanFactory.isFactoryBean(beanNameToRegister);
            String beanNameToCheck = isFactoryBean ? "&" + beanName : beanName;
            if (this.beanFactory.containsSingleton(beanNameToRegister) && (!isFactoryBean || Lifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck))) || SmartLifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck))) {
                Lifecycle bean = (Lifecycle)this.beanFactory.getBean(beanNameToCheck, Lifecycle.class);
                if (bean != this) {
                    beans.put(beanNameToRegister, bean);
                }
            }
        }

        return beans;
    }

    protected int getPhase(Lifecycle bean) {
        return bean instanceof Phased ? ((Phased)bean).getPhase() : 0;
    }

    private class LifecycleGroupMember implements Comparable<DefaultLifecycleProcessor.LifecycleGroupMember> {
        private final String name;
        private final Lifecycle bean;

        LifecycleGroupMember(String name, Lifecycle bean) {
            this.name = name;
            this.bean = bean;
        }

        public int compareTo(DefaultLifecycleProcessor.LifecycleGroupMember other) {
            int thisOrder = DefaultLifecycleProcessor.this.getPhase(this.bean);
            int otherOrder = DefaultLifecycleProcessor.this.getPhase(other.bean);
            return thisOrder == otherOrder ? 0 : (thisOrder < otherOrder ? -1 : 1);
        }
    }

    private class LifecycleGroup {
        private final List<DefaultLifecycleProcessor.LifecycleGroupMember> members = new ArrayList();
        private final int phase;
        private final long timeout;
        private final Map<String, ? extends Lifecycle> lifecycleBeans;
        private final boolean autoStartupOnly;
        private volatile int smartMemberCount;

        public LifecycleGroup(int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {
            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }

        public void add(String name, Lifecycle bean) {
            if (bean instanceof SmartLifecycle) {
                ++this.smartMemberCount;
            }

            this.members.add(DefaultLifecycleProcessor.this.new LifecycleGroupMember(name, bean));
        }

        public void start() {
            if (!this.members.isEmpty()) {
                if (DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                    DefaultLifecycleProcessor.this.logger.info("Starting beans in phase " + this.phase);
                }

                Collections.sort(this.members);
                Iterator var1 = this.members.iterator();

                while(var1.hasNext()) {
                    DefaultLifecycleProcessor.LifecycleGroupMember member = (DefaultLifecycleProcessor.LifecycleGroupMember)var1.next();
                    if (this.lifecycleBeans.containsKey(member.name)) {
                        DefaultLifecycleProcessor.this.doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
                    }
                }

            }
        }

        public void stop() {
            if (!this.members.isEmpty()) {
                if (DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                    DefaultLifecycleProcessor.this.logger.info("Stopping beans in phase " + this.phase);
                }

                Collections.sort(this.members, Collections.reverseOrder());
                CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
                Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet());
                Iterator var3 = this.members.iterator();

                while(var3.hasNext()) {
                    DefaultLifecycleProcessor.LifecycleGroupMember member = (DefaultLifecycleProcessor.LifecycleGroupMember)var3.next();
                    if (this.lifecycleBeans.containsKey(member.name)) {
                        DefaultLifecycleProcessor.this.doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                    } else if (member.bean instanceof SmartLifecycle) {
                        latch.countDown();
                    }
                }

                try {
                    latch.await(this.timeout, TimeUnit.MILLISECONDS);
                    if (latch.getCount() > 0L && !countDownBeanNames.isEmpty() && DefaultLifecycleProcessor.this.logger.isWarnEnabled()) {
                        DefaultLifecycleProcessor.this.logger.warn("Failed to shut down " + countDownBeanNames.size() + " bean" + (countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " + this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
                    }
                } catch (InterruptedException var5) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }
}