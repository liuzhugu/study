package org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean;

import org.liuzhugu.javastudy.practice.rpc.complex.config.registry.RedisRegistryCenter;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.ServerConfig;
import org.liuzhugu.javastudy.practice.rpc.complex.network.domain.LocalServerInfo;
import org.liuzhugu.javastudy.practice.rpc.complex.network.server.ServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 注册中心
 * */
public class ServerBean extends ServerConfig implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(ServerBean.class);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //启动注册中心
        logger.info("启动注册中心 ...");
        RedisRegistryCenter.init(host, port);
        logger.info("启动注册中心完成 {} {}", host, port);

        //初始化服务端
        logger.info("初始化生产端服务 ...");
        ServerSocket serverSocket = new ServerSocket(applicationContext);
        Thread thread = new Thread(serverSocket);
        thread.start();
        while (! serverSocket.isActiveSocketServer()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ingore) {

            }
        }

        logger.info("初始化生产端服务完成 {} {}", LocalServerInfo.LOCAL_HOST, LocalServerInfo.LOCAL_PORT);

    }
}
