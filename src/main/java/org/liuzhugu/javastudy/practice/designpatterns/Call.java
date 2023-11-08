package org.liuzhugu.javastudy.practice.designpatterns;

import java.util.concurrent.CountDownLatch;

//代表正在执行中 或已经结束的任务
public class Call {
    private byte[] val;
    private CountDownLatch cld;

    public byte[] getVal() {
        return val;
    }

    public void setVal(byte[] val) {
        this.val = val;
    }

    //尝试挂起   如果结果已经准备好  那么不用挂起
    public void  await(){
        try {
            this.cld.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //选中的线程去完成任务  让其他线程阻塞
    public void lock() {
        this.cld = new CountDownLatch(1);
    }

    //选中的线程完成了任务   唤醒阻塞线程
    public void done() {
        this.cld.countDown();
    }
}