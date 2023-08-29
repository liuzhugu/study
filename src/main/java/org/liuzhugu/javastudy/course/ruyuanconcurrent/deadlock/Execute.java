package org.liuzhugu.javastudy.course.ruyuanconcurrent.deadlock;

public class Execute implements Runnable {

    private Account from;
    private Account to;
    private int count;

    public Execute(Account from, Account to, int count) {
        this.from = from;
        this.to = to;
        this.count = count;
    }

    @Override
    public void run() {
        System.out.println(from.toString() + " to " + to.toString() + " start");
        from.transfer(to,count);
    }
}
