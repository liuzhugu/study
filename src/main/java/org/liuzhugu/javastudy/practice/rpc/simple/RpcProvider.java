package org.liuzhugu.javastudy.practice.rpc.simple;


import org.liuzhugu.javastudy.practice.rpc.simple.service.HelloService;
import org.liuzhugu.javastudy.practice.rpc.simple.service.impl.HelloServiceImpl;

/**
 * 暴露服务
 * */
public class RpcProvider {

    public static void main(String[] args)throws Exception{
        HelloService helloService=new HelloServiceImpl();
        RpcFramework.export(helloService,1234);
    }
}
