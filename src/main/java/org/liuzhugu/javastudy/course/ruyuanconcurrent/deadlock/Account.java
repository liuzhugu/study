package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;

public class Account {
    private Allocator allocator = DeadLock2.getInstance();

    private int balance;

    void transfer(Account target,int amt) {
        //死等锁   直到两个锁都没被占用才会获取到锁
        int index = 0;
        while (!allocator.apply(this,target,this.toString(),index ++)) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            synchronized (this) {
                System.out.println(this.toString() + " lock obj1");
                synchronized (target) {
                    System.out.println(this.toString() + " lock obj2");
                    //模拟实际业务耗时
                    for (int i = 0;i < 10;i ++) {
                        System.out.println(this.toString() + " is working");
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            //执行完毕后  再是否所有资源
            allocator.clean(this,target);
            System.out.println(this.toString() + "  end");
        }
    }
}
