package org.liuzhugu.javastudy.book.springinaction.chapter2;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//加载第二步:开启自动扫描,默认扫描跟配置类相同的包,扫描有Component注解标记的类
//@ComponentScan
//指定扫描范围,这样配置类跟要扫描的类不在一个包下也能扫描了
//@ComponentScan(value = "org.liuzhugu.javastudy.book.springinaction.chapter2")
//强调扫描范围是基础包,并且可以指定多个
//@ComponentScan(basePackages = {"org.liuzhugu.javastudy.book.springinaction.chapter2","org.liuzhugu.javastudy.book.springinaction.chapter1"})
//按类指定,这样即使包名变了,还是能扫描
@ComponentScan(basePackageClasses = {CDPlayer.class,SgtPeppers.class})
@Configuration
public class CDPlayerConfig {
}
