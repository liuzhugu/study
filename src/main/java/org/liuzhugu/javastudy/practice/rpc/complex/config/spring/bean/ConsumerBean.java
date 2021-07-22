package org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFuture;
import org.liuzhugu.javastudy.practice.rpc.complex.config.reflect.JDKProxy;
import org.liuzhugu.javastudy.practice.rpc.complex.config.registry.RedisRegistryCenter;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.ConsumerConfig;
import org.liuzhugu.javastudy.practice.rpc.complex.network.client.ClientSocket;
import org.liuzhugu.javastudy.practice.rpc.complex.network.domain.RpcProviderConfig;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Request;
import org.liuzhugu.javastudy.practice.rpc.complex.util.ClassLoaderUtils;
import org.springframework.beans.factory.FactoryBean;

/**
 * 消费者
 * */
public class ConsumerBean<T> extends ConsumerConfig implements FactoryBean {

    private ChannelFuture future;

    private RpcProviderConfig providerConfig;

    //扫描配置生成一个增强的类
    @Override
    public Object getObject() throws Exception {
        //从redis获取服务提供者
        if (providerConfig == null) {
            String infoStr = RedisRegistryCenter.obtainProvider(nozzle,alias);
            providerConfig = JSON.parseObject(infoStr,RpcProviderConfig.class);
        }

        assert null != providerConfig;

        //获取通信的channel
        if (future == null) {
            ClientSocket clientSocket = new ClientSocket(providerConfig.getHost(),providerConfig.getPort());
            new Thread(clientSocket).start();
            for (int i = 0;i < 100;i ++) {
                if (future == null) break;
                Thread.sleep(500);
                future = clientSocket.getFuture();
            }
        }

        //
        Request request = new Request();
        request.setChannel(future.channel());
        request.setNozzle(nozzle);
        request.setRef(providerConfig.getRef());
        request.setAlias(alias);
        return JDKProxy.getProxy(ClassLoaderUtils.forName(nozzle),request);
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return ClassLoaderUtils.forName(nozzle);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
