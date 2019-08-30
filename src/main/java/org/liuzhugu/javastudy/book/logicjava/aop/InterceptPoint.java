package org.liuzhugu.javastudy.book.logicjava.aop;

//切点枚举
public enum  InterceptPoint {
    //调用前
    BEFORE,
    //调用后
    AFTER,
    //出现异常
    EXCEPTION
}
