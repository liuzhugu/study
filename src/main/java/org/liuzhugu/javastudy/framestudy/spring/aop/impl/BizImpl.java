package org.liuzhugu.javastudy.framestudy.spring.aop.impl;

import org.liuzhugu.javastudy.framestudy.spring.aop.Biz;

public class BizImpl implements Biz {
    //声明为final后  那么就不会考虑子类的覆盖
    //public final void help() {
    public void help() {
        System.out.println("买书");
    }

    public void service() {
        System.out.println("提供买书服务");
    }
}
