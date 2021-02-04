package org.liuzhugu.javastudy.practice.concurrent;

/**
 * 任务
 * */
public interface Executor {
    public void execute(Runnable r);
}
