package org.liuzhugu.javastudy.practice.rpc.complex.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.liuzhugu.javastudy.practice.rpc.complex.network.codec.RpcDecoder;
import org.liuzhugu.javastudy.practice.rpc.complex.network.codec.RpcEncoder;
import org.liuzhugu.javastudy.practice.rpc.complex.network.domain.LocalServerInfo;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Request;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Response;
import org.liuzhugu.javastudy.practice.rpc.complex.util.NetUtil;
import org.springframework.context.ApplicationContext;

public class ServerSocket implements Runnable {
    private ChannelFuture channelFuturel;

    private ApplicationContext applicationContext;
    public ServerSocket(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean isActiveSocketServer() {
        try {
            if (channelFuturel != null) {
                return channelFuturel.channel().isActive();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new RpcDecoder(Request.class),
                                    new RpcEncoder(Response.class),
                                    new ServerHandler(applicationContext));
                        }
                    });
            //启动初始端口
            int port = 22201;
            while (NetUtil.isPortUsing(port)) {
                port++;
            }
            LocalServerInfo.LOCAL_HOST = NetUtil.getHost();
            LocalServerInfo.LOCAL_PORT = port;
            //注册服务
            this.channelFuturel = bootstrap.bind(port).sync();
            this.channelFuturel.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
