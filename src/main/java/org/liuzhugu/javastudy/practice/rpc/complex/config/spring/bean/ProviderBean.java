package org.liuzhugu.javastudy.practice.rpc.complex.config.spring.bean;

import com.alibaba.fastjson.JSON;
import org.liuzhugu.javastudy.practice.rpc.complex.config.registry.RedisRegistryCenter;
import org.liuzhugu.javastudy.practice.rpc.complex.config.spring.ProviderConfig;
import org.liuzhugu.javastudy.practice.rpc.complex.network.domain.LocalServerInfo;
import org.liuzhugu.javastudy.practice.rpc.complex.network.domain.RpcProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 服务提供者
 * */
public class ProviderBean extends ProviderConfig implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(ProviderBean.class);

    //注册服务提供者
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        RpcProviderConfig config = new RpcProviderConfig();
        config.setNozzle(nozzle);
        config.setAlias(alias);
        config.setRef(ref);
        config.setHost(LocalServerInfo.LOCAL_HOST);
        config.setPort(LocalServerInfo.LOCAL_PORT);
        long count = RedisRegistryCenter.registryProvider(nozzle,alias, JSON.toJSONString(config));

        logger.info("注册生产者：{} {} {}", nozzle, alias, count);
    }

}
