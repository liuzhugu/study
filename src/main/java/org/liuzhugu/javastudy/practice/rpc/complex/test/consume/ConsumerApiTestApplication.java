package org.liuzhugu.javastudy.practice.rpc.complex.test.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath:/rpc/spring-config.xml")
public class ConsumerApiTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApiTestApplication.class, args);
    }

}
