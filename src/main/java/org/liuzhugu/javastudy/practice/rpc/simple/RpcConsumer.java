package org.liuzhugu.javastudy.practice.rpc.simple;

import org.liuzhugu.javastudy.practice.rpc.simple.service.HelloService;

/**
 * 引用服务
 * */
public class RpcConsumer {
    public static void main(String[] args)throws Exception{
        HelloService service=RpcFramework.refer(HelloService.class,"132.232.151.95",1234);
        for(int i=0;i<100;i++){
            String hello=service.sayHello("world"+i);
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }
}
