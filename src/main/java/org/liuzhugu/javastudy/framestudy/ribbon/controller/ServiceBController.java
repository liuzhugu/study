package org.liuzhugu.javastudy.framestudy.ribbon.controller;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/serviceB")
public class ServiceBController {

    @Bean
    //修饰的RestTemplate  获取调用LoadBalancerClient
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping(value = "greeting/{name}",method = RequestMethod.GET)
    public String greeting(@PathVariable("name") String name) {
        RestTemplate restTemplate = getRestTemplate();
        return restTemplate.getForObject("http://ServiceA/sayHello/" + name,String.class);
    }
}
