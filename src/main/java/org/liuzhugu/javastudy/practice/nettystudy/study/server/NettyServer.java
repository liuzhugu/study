package org.liuzhugu.javastudy.practice.nettystudy.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.PacketDecoder;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.PacketEncoder;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.Spliter;
import org.liuzhugu.javastudy.practice.nettystudy.study.server.handle.AuthHandler;
import org.liuzhugu.javastudy.practice.nettystudy.study.server.handle.FirstServerHandler;
import org.liuzhugu.javastudy.practice.nettystudy.study.server.handle.LoginRequestHandler;
import org.liuzhugu.javastudy.practice.nettystudy.study.server.handle.MessageRequestHandler;

import java.util.Date;

public class NettyServer {

    private static final int PORT = 3000;


    public static void main(String[] args){
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup work=new NioEventLoopGroup();


        serverBootstrap
                .group(boss,work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch){
                        //ch.pipeline().addLast(new FirstServerHandler());
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new LoginRequestHandler());
                        ch.pipeline().addLast(new AuthHandler());
                        ch.pipeline().addLast(new MessageRequestHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        bind(serverBootstrap, PORT);

    }
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
