package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.*;

class PostProcessorRegistrationDelegate {
    PostProcessorRegistrationDelegate() {
    }

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        Set<String> processedBeans = new HashSet();
        int var9;
        ArrayList currentRegistryProcessors;
        String[] postProcessorNames;
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new LinkedList();
            Iterator var6 = beanFactoryPostProcessors.iterator();

            while(var6.hasNext()) {
                BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)var6.next();
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor)postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }

            currentRegistryProcessors = new ArrayList();
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            String[] var18 = postProcessorNames;
            var9 = postProcessorNames.length;

            int var10;
            String ppName;
            for(var10 = 0; var10 < var9; ++var10) {
                ppName = var18[var10];
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }

            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            currentRegistryProcessors.clear();
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            var18 = postProcessorNames;
            var9 = postProcessorNames.length;

            for(var10 = 0; var10 < var9; ++var10) {
                ppName = var18[var10];
                if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }

            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            currentRegistryProcessors.clear();
            boolean reiterate = true;

            while(reiterate) {
                reiterate = false;
                postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                String[] var21 = postProcessorNames;
                var10 = postProcessorNames.length;

                for(int var28 = 0; var28 < var10; ++var28) {
                    ppName = var21[var28];
                    if (!processedBeans.contains(ppName)) {
                        currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                        processedBeans.add(ppName);
                        reiterate = true;
                    }
                }

                sortPostProcessors(currentRegistryProcessors, beanFactory);
                registryProcessors.addAll(currentRegistryProcessors);
                invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
                currentRegistryProcessors.clear();
            }

            invokeBeanFactoryPostProcessors((Collection)registryProcessors, (ConfigurableListableBeanFactory)beanFactory);
            invokeBeanFactoryPostProcessors((Collection)regularPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        } else {
            invokeBeanFactoryPostProcessors((Collection)beanFactoryPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        }

        postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList();
        List<String> orderedPostProcessorNames = new ArrayList();
        currentRegistryProcessors = new ArrayList();
        postProcessorNames = postProcessorNames;
        int var22 = postProcessorNames.length;

        String ppName;
        for(var9 = 0; var9 < var22; ++var9) {
            ppName = postProcessorNames[var9];
            if (!processedBeans.contains(ppName)) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
                } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                    orderedPostProcessorNames.add(ppName);
                } else {
                    currentRegistryProcessors.add(ppName);
                }
            }
        }

        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors((Collection)priorityOrderedPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList();
        Iterator var23 = orderedPostProcessorNames.iterator();

        while(var23.hasNext()) {
            String postProcessorName = (String)var23.next();
            orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }

        sortPostProcessors(orderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors((Collection)orderedPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList();
        Iterator var26 = currentRegistryProcessors.iterator();

        while(var26.hasNext()) {
            ppName = (String)var26.next();
            nonOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
        }

        invokeBeanFactoryPostProcessors((Collection)nonOrderedPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        beanFactory.clearMetadataCache();
    }


    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor(new PostProcessorRegistrationDelegate.BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList();
        List<BeanPostProcessor> internalPostProcessors = new ArrayList();
        List<String> orderedPostProcessorNames = new ArrayList();
        List<String> nonOrderedPostProcessorNames = new ArrayList();
        String[] var8 = postProcessorNames;
        int var9 = postProcessorNames.length;

        String ppName;
        BeanPostProcessor pp;
        for(int var10 = 0; var10 < var9; ++var10) {
            ppName = var8[var10];
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            } else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }

        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)priorityOrderedPostProcessors);
        List<BeanPostProcessor> orderedPostProcessors = new ArrayList();
        Iterator var14 = orderedPostProcessorNames.iterator();

        while(var14.hasNext()) {
            ppName = (String)var14.next();
            pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
            orderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }

        sortPostProcessors(orderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)orderedPostProcessors);
        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList();
        Iterator var17 = nonOrderedPostProcessorNames.iterator();

        while(var17.hasNext()) {
            ppName = (String)var17.next();
            pp = (BeanPostProcessor) beanFactory.getBean(ppName, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }

        //注册后继处理器
        registerBeanPostProcessors(beanFactory, (List)nonOrderedPostProcessors);
        sortPostProcessors(internalPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)internalPostProcessors);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory)beanFactory).getDependencyComparator();
        }

        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }

        Collections.sort(postProcessors, (Comparator)comparatorToUse);
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
        Iterator var2 = postProcessors.iterator();

        while(var2.hasNext()) {
            BeanDefinitionRegistryPostProcessor postProcessor = (BeanDefinitionRegistryPostProcessor)var2.next();
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }

    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        Iterator var2 = postProcessors.iterator();

        while(var2.hasNext()) {
            BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)var2.next();
            postProcessor.postProcessBeanFactory(beanFactory);
        }

    }

    private static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
        Iterator var2 = postProcessors.iterator();

        while(var2.hasNext()) {
            BeanPostProcessor postProcessor = (BeanPostProcessor)var2.next();
            beanFactory.addBeanPostProcessor(postProcessor);
        }

    }

    private static class BeanPostProcessorChecker implements BeanPostProcessor {
        private static final Log logger = LogFactory.getLog(PostProcessorRegistrationDelegate.BeanPostProcessorChecker.class);
        private final ConfigurableListableBeanFactory beanFactory;
        private final int beanPostProcessorTargetCount;

        public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }

        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean != null && !(bean instanceof BeanPostProcessor) && !this.isInfrastructureBean(beanName) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && logger.isInfoEnabled()) {
                logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() + "] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)");
            }

            return bean;
        }

        private boolean isInfrastructureBean(String beanName) {
            if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
                BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
                return bd.getRole() == 2;
            } else {
                return false;
            }
        }
    }
}
