package org.liuzhugu.javastudy.practice.designpatterns.chain;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

public class ChainTest {
    public static void main(String[] args) {
        //建立责任链
        FilterChain filterChain = new FilterChain(new Study());
        //建立待处理对象
        PreparationList preparationList = new PreparationList();
        preparationList.setHaveBreakfast(false);
        preparationList.setBrushTeeth(true);
        preparationList.setWashFace(true);
        preparationList.setSayHello(true);
        //获取所有的准备工作任务
        ClassPathXmlApplicationContext context = new
                ClassPathXmlApplicationContext("/designpatterns/chain/bean.xml");
        Map<String,StudyPrepareFilter> filterList = context.getBeansOfType(StudyPrepareFilter.class);
        if (filterList != null && filterList.size() != 0) {
            for (Map.Entry<String,StudyPrepareFilter> entry : filterList.entrySet()) {
                filterChain.addFilter(entry.getValue());
            }
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
