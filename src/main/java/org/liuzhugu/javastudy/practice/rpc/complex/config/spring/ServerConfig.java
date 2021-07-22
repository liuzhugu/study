package org.liuzhugu.javastudy.practice.rpc.complex.config.spring;


/**
 * 注册中心配置
 * */
public class ServerConfig {
    protected String host; //注册中心地址
    protected int port;   ///端口号

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
