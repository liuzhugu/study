package org.liuzhugu.javastudy.practice.designpatterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class HaveBreakfastFilter implements StudyPrepareFilter {
    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        if (preparationList.isHaveBreakfast()) {
            System.out.println("吃完早餐");
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
