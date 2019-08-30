package org.liuzhugu.javastudy.book.springinaction.chapter1;

import org.aspectj.lang.annotation.Pointcut;

import java.io.PrintStream;


public class Minstrel {
    //输出方向依赖注入的对象
    private PrintStream printStream;

    public Minstrel(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Pointcut()
    public void singBeforeQuest() {
        printStream.println("这位骑士是如此之勇敢");
    }
    public void singAfterQuest() {
        printStream.println("这位勇士勇敢地完成了冒险");
    }
}
