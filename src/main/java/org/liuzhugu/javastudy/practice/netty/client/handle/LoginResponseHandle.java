package org.liuzhugu.javastudy.practice.netty.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LoginRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LoginResponsePacket;

import java.util.Date;
import java.util.UUID;

public class LoginResponseHandle extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUsername("flash");
        loginRequestPacket.setPassword("pwd");

        // 写数据
        ctx.channel().writeAndFlush(loginRequestPacket);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LoginResponsePacket loginResponsePacket){
        // 登录流程
        if(loginResponsePacket.isSuccess()){
            System.out.println(new Date() + ": 客户端登录成功");
        }else {
            System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
        }
    }
}
