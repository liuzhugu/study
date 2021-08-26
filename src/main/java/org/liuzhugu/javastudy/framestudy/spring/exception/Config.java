package org.liuzhugu.javastudy.framestudy.spring.exception;

import org.springframework.context.annotation.Bean;

public class Config {
    @Bean
    public Rumenz rumenz1() {
        Rumenz r = new Rumenz();
        r.setId(456);
        r.setName("名字");
        return r;
    }

    @Bean
    public Rumenz rumenz() {
        Rumenz r = new Rumenz();
        r.setId(123);
        r.setName("名字");
        return r;
    }
}
