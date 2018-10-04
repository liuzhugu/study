package org.liuzhugu.javastudy.javaA.concurrency;

/**
 * Created by liuting6 on 2018/1/12.
 * 打印线程栈
 */
public class ThreadStackTest {
    public static void main(String[] args){
        printStack(getStackByThread());
        printStack(getStackByException());
    }
    private static void printStack(StackTraceElement[] stacks){
        for(StackTraceElement stack:stacks){
            System.out.println(stack);
        }
        System.out.println("\n");
    }
    private static StackTraceElement[] getStackByThread(){
        return Thread.currentThread().getStackTrace();
    }
    private static StackTraceElement[] getStackByException(){
        return Thread.currentThread().getStackTrace();
    }
}
