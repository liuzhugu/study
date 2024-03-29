package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultAdvisorAdapterRegistry implements AdvisorAdapterRegistry, Serializable {
    private final List<AdvisorAdapter> adapters = new ArrayList(3);

    public DefaultAdvisorAdapterRegistry() {
        this.registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
        this.registerAdvisorAdapter(new AfterReturningAdviceAdapter());
        this.registerAdvisorAdapter(new ThrowsAdviceAdapter());
    }

    public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
        if (adviceObject instanceof Advisor) {
            return (Advisor)adviceObject;
        } else if (!(adviceObject instanceof Advice)) {
            throw new UnknownAdviceTypeException(adviceObject);
        } else {
            Advice advice = (Advice)adviceObject;
            if (advice instanceof MethodInterceptor) {
                return new DefaultPointcutAdvisor(advice);
            } else {
                Iterator var3 = this.adapters.iterator();

                AdvisorAdapter adapter;
                do {
                    if (!var3.hasNext()) {
                        throw new UnknownAdviceTypeException(advice);
                    }

                    adapter = (AdvisorAdapter)var3.next();
                } while(!adapter.supportsAdvice(advice));

                return new DefaultPointcutAdvisor(advice);
            }
        }
    }

    public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
        List<MethodInterceptor> interceptors = new ArrayList(3);
        Advice advice = advisor.getAdvice();
        if (advice instanceof MethodInterceptor) {
            interceptors.add((MethodInterceptor)advice);
        }

        Iterator var4 = this.adapters.iterator();

        while(var4.hasNext()) {
            AdvisorAdapter adapter = (AdvisorAdapter)var4.next();
            if (adapter.supportsAdvice(advice)) {
                interceptors.add(adapter.getInterceptor(advisor));
            }
        }

        if (interceptors.isEmpty()) {
            throw new UnknownAdviceTypeException(advisor.getAdvice());
        } else {
            return (MethodInterceptor[])interceptors.toArray(new MethodInterceptor[interceptors.size()]);
        }
    }

    public void registerAdvisorAdapter(AdvisorAdapter adapter) {
        this.adapters.add(adapter);
    }
}
