package org.liuzhugu.javastudy.practice.rpc.complex.test.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath:/rpc/spring-config.xml")
public class ProviderApiTestController {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApiTestController.class, args);
    }

}
