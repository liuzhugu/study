package org.liuzhugu.javastudy.practice.designpatterns.chain;


import java.util.ArrayList;
import java.util.List;

public class FilterChain implements StudyPrepareFilter {

    private int pos = 0;

    private Study study;

    private List<StudyPrepareFilter> studyPrepareFilters =  new ArrayList<>();

    public FilterChain(Study study) {
        this.study = study;
    }

    public void addFilter(StudyPrepareFilter studyPrepareFilter) {
        studyPrepareFilters.add(studyPrepareFilter);
    }

    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        //全部都执行完之后可以开始学习,否则都继续执行下一链
        if (pos == studyPrepareFilters.size()) {
            study.study();
            return;
        }
        studyPrepareFilters.get(pos ++).doFilter(preparationList,filterChain);
    }
}
