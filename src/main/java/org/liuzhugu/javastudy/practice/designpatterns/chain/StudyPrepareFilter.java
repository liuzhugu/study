package org.liuzhugu.javastudy.practice.designpatterns.chain;

import org.liuzhugu.javastudy.practice.designpatterns.chain.FilterChain;
import org.liuzhugu.javastudy.practice.designpatterns.chain.PreparationList;

/**
 * 准备工作的责任链
 * */
public interface StudyPrepareFilter {
    void doFilter(PreparationList preparationList, FilterChain filterChain);
}
