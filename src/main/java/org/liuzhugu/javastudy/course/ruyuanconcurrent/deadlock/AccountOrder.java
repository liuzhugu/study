package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;

public class AccountOrder {
    //获取锁必须按顺序   这样后面的锁就不会被人抢占了
    private int id;
    private int blance;
    void transfer(AccountOrder target,int amt) {
        AccountOrder left = this;
        AccountOrder right = target;
        if (left.id > right.id) {
            //调整顺序上锁顺序
            left = target;
            right = this;
        }
        //开始按顺序占用锁  只要成功占用前面的锁  后面的锁肯定能占用
        synchronized (left) {
            synchronized (right) {
                if (this.blance > amt) {
                    this.blance -= amt;
                    target.blance += amt;
                }
            }
        }
    }

}
