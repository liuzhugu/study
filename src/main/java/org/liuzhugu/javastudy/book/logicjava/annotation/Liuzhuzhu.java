package org.liuzhugu.javastudy.book.logicjava.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//注解作用的目标   作用于类上
@Target({TYPE})
//注解信息保留到什么时候   只在源码中
@Retention(RetentionPolicy.SOURCE)
//
public @interface Liuzhuzhu {
}
