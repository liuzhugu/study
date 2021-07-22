package org.liuzhugu.javastudy.practice.rpc.complex.annotation;


import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.ServerAutoConfiguration;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ServerAutoConfiguration.class})
@EnableConfigurationProperties(ServerProperties.class)
@ComponentScan("org.liuzhugu.javastudy.practice.rpc.complex.*")
public @interface EnableRpc {
}
