package org.liuzhugu.javastudy.book.springinaction.chapter1;

import java.io.PrintStream;

/**
 * 杀死恶龙的冒险
 * */
public class SlayDragonQuest implements Quest {

    //输出方向依赖注入的对象
    private PrintStream printStream;
    public SlayDragonQuest(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void embark() {
        printStream.println("正在进行消灭恶龙的冒险!");
    }
}
