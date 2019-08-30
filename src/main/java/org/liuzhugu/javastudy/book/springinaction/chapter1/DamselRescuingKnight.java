package org.liuzhugu.javastudy.book.springinaction.chapter1;


public class DamselRescuingKnight {

    /**
     * DamselRescuingKnight只能执行RescueDamselQuest,强耦合
     * */
    private RescueDamselQuest rescueDamselQuest;

    public DamselRescuingKnight(RescueDamselQuest rescueDamselQuest) {
        this.rescueDamselQuest = rescueDamselQuest;
    }
    public void embarkOnQuest(){
        rescueDamselQuest.embark();
    }
}
