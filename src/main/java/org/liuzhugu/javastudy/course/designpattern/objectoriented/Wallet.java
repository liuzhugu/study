package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.math.BigDecimal;

public class Wallet {
    private long createTime;
    private BigDecimal balance;
    private long balanceLastModifiedTime;

    public Wallet() {
        this.createTime = System.currentTimeMillis();
        this.balance = BigDecimal.ZERO;
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }

    //只有访问方法  没有设置方法   配合private   那么就无法改变属性值了
    public long getCreateTime() {
        return createTime;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public long getBalanceLastModifiedTime() {
        return balanceLastModifiedTime;
    }

    public void increaseBalance(BigDecimal increasedAmount) throws InvalidAmountException {
        if (increasedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("");
        }
        this.balance.add(increasedAmount);
        //只有balance修改 balanceLastModifiedTime才会修改   因此封装在这里
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }

    public void decreaseBalance(BigDecimal decreasedAmount) throws InsufficientAmountException,InvalidAmountException {
        if (decreasedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("...");
        }
        if (decreasedAmount.compareTo(this.balance) > 0) {
            throw new InsufficientAmountException("...");
        }
        this.balance.subtract(decreasedAmount);
        //只有balance修改 balanceLastModifiedTime才会修改   因此封装在这里
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }
}
