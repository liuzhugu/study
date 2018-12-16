package org.liuzhugu.javastudy.practice.netty.study.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.study.common.Session;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.netty.study.util.SessionUtil;

public class LoginResponseHandle extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LoginResponsePacket loginResponsePacket){
        int userId = loginResponsePacket.getUserId();
        String userName = loginResponsePacket.getUserName();

        if(loginResponsePacket.isSuccess()){
            System.out.println("[" + userName + "]登录成功，userId 为: " + loginResponsePacket.getUserId());
            SessionUtil.bindSession(new Session(userId,userName),ctx.channel());
        }else {
            System.out.println("[" + userName + "]登录失败，原因：" + loginResponsePacket.getReason());
        }
        System.out.println();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println("客户端连接被关闭");
    }
}
