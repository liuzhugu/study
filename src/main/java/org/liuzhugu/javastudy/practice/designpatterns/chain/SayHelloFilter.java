package org.liuzhugu.javastudy.practice.designpatterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class SayHelloFilter implements StudyPrepareFilter {
    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        if (preparationList.isSayHello()) {
            System.out.println("对妈妈说再见");
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
