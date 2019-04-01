package org.liuzhugu.javastudy.book.springinaction.chapter1;

public class BraveKnight implements Knight {

    //与Quest耦合,面向接口编程
    //只要是实现了Quest接口的实现类都可以自由替换
    private Quest quest;
    public BraveKnight(Quest quest) {
        this.quest = quest;
    }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }
}
