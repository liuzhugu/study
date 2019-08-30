package org.liuzhugu.javastudy.book.springinaction.chapter1;

public class BraveKnight implements Knight {

    //与Quest耦合,面向接口编程
    //只要是实现了Quest接口的实现类都可以自由替换
    private Quest quest;
    public BraveKnight(Quest quest) {
        this.quest = quest;
    }

    @Override
    //在这个方法执行前后调用了其他方法,并且在该class内没有任何感知
    //切片切入的代码什么时候加上修改去掉都没有任何感知,不会影响到该方法
    public void embarkOnQuest() {
        quest.embark();
    }
}
