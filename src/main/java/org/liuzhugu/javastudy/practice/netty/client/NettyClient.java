package org.liuzhugu.javastudy.practice.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.liuzhugu.javastudy.practice.netty.client.console.ConsoleCommandManager;
import org.liuzhugu.javastudy.practice.netty.client.console.LoginConsoleCommand;
import org.liuzhugu.javastudy.practice.netty.client.handle.CreateGroupResponseHandle;
import org.liuzhugu.javastudy.practice.netty.client.handle.LoginResponseHandle;
import org.liuzhugu.javastudy.practice.netty.client.handle.LogoutResponseHandle;
import org.liuzhugu.javastudy.practice.netty.client.handle.MessageResponseHandle;
import org.liuzhugu.javastudy.practice.netty.protocol.PacketDecoder;
import org.liuzhugu.javastudy.practice.netty.protocol.PacketEncoder;
import org.liuzhugu.javastudy.practice.netty.protocol.Spliter;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class NettyClient {

    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3000;

    public static void main(String[] args){
        Bootstrap bootstrap=new Bootstrap();
        NioEventLoopGroup workerGroup=new NioEventLoopGroup();

        bootstrap
                // 1.指定线程模型
                .group(workerGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                // 3.IO 处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch){
                        // 指定连接数据读写逻辑
                        //责任链模式.通过责任链，在不同步骤上进行不同处理，不同环节的handle可以灵活替换，只有参数配的环才会处理
                        // 处理完又会把结果传给下一环，然后读取数据和写数据都会经过责任链上的环环处理
                        // 当然一些handle比如登陆处理部分，只在第一次时需要执行，那么执行成功之后就可以把这个handle去掉了
                        // 避免后面每一次执行通过经过它，从而实现handle的热插拔

                        //自定义解析拆包实现
                        ch.pipeline().addLast(new Spliter());
                        //编码
                        ch.pipeline().addLast(new PacketDecoder());
                        //如果是登陆请求，在login中的channelRead0里参数类型配,如果是信息才会在message中匹配参数
                        ch.pipeline().addLast(new LoginResponseHandle());
                        //创建群组命令处理
                        ch.pipeline().addLast(new CreateGroupResponseHandle());
                        ch.pipeline().addLast(new MessageResponseHandle());
                        ch.pipeline().addLast(new LogoutResponseHandle());
                        //解码
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        //开始连接
        connect(bootstrap,HOST,PORT,MAX_RETRY);
    }

    //连接失败后，等待一段时间继续重连，每次失败后等待时间延长
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 连接成功，启动控制台线程……");
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        Scanner sc=new Scanner(System.in);
        LoginConsoleCommand loginConsoleCommand=new LoginConsoleCommand();
        ConsoleCommandManager consoleCommandManager=new ConsoleCommandManager();

        new Thread(() -> {
            while (!Thread.interrupted()) {
                if(!SessionUtil.hasLogin(channel)){
                    loginConsoleCommand.exec(sc,channel);
                }else {
                    consoleCommandManager.exec(sc,channel);
                }
            }
        }).start();
    }

    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }
}
