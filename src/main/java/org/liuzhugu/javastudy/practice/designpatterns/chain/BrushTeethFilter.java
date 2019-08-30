package org.liuzhugu.javastudy.practice.designpatterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class BrushTeethFilter implements StudyPrepareFilter {
    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        if (preparationList.isBrushTeeth()) {
            System.out.println("刷完牙");
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
