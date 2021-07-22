package org.liuzhugu.javastudy.practice.rpc.complex.config.spring;

/**
 * 消费者配置
 * */
public class ConsumerConfig {
    protected String nozzle;//接口
    protected String alias;//别名

    public String getNozzle() {
        return nozzle;
    }

    public void setNozzle(String nozzle) {
        this.nozzle = nozzle;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
