package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.MethodMatchers;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {
    public DefaultAdvisorChainFactory() {
    }

    /**
     * ￥ 获取拦截器
     * */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, Class<?> targetClass) {
        List<Object> interceptorList = new ArrayList(config.getAdvisors().length);
        Class<?> actualClass = targetClass != null ? targetClass : method.getDeclaringClass();
        boolean hasIntroductions = hasMatchingIntroductions(config, actualClass);
        AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        Advisor[] var8 = config.getAdvisors();
        int var9 = var8.length;

        //遍历所有的 Advisor
        for(int var10 = 0; var10 < var9; ++var10) {
            Advisor advisor = var8[var10];
            MethodInterceptor[] interceptors;
            //1.如果是PointcutAdvisor  那么类和方法匹配才会将拦截器加入到返回的拦截器列表
            //2.如果是IntroductionAdvisor  只要类匹配就会将拦截器加入到返回的拦截器列表
            //3.如果是其他类型 无条件将其拦截器加入到返回的拦截器列表


            //1.如果是PointcutAdvisor  类和方法匹配  才会将拦截器加入到返回列表
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor)advisor;
                if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    interceptors = registry.getInterceptors(advisor);
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    if (MethodMatchers.matches(mm, method, actualClass, hasIntroductions)) {
                        if (mm.isRuntime()) {
                            MethodInterceptor[] var15 = interceptors;
                            int var16 = interceptors.length;

                            for(int var17 = 0; var17 < var16; ++var17) {
                                MethodInterceptor interceptor = var15[var17];
                                interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                            }
                        } else {
                            interceptorList.addAll(Arrays.asList(interceptors));
                        }
                    }
                }
            }
            //2. 如果是IntroductionAdvisor  只要类匹配就会将拦截器加入到返回的拦截器列表
            else if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
                if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
                    interceptors = registry.getInterceptors(advisor);
                    interceptorList.addAll(Arrays.asList(interceptors));
                }
            }
            //3.其他类型 无条件将其拦截器加入到返回的拦截器列表
            else {
                interceptors = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors));
            }
        }

        return interceptorList;
    }

    private static boolean hasMatchingIntroductions(Advised config, Class<?> actualClass) {
        for(int i = 0; i < config.getAdvisors().length; ++i) {
            Advisor advisor = config.getAdvisors()[i];
            if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
                if (ia.getClassFilter().matches(actualClass)) {
                    return true;
                }
            }
        }

        return false;
    }
}