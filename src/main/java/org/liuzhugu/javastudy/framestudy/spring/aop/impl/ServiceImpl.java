package org.liuzhugu.javastudy.framestudy.spring.aop.impl;

import org.liuzhugu.javastudy.framestudy.spring.aop.Service;

public class ServiceImpl implements Service {
    @Override
    public void help() {
        System.out.println("买书");
    }
}
