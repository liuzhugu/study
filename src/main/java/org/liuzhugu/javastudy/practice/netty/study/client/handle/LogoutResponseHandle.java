package org.liuzhugu.javastudy.practice.netty.study.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.study.common.Session;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.LogoutResponsePacket;
import org.liuzhugu.javastudy.practice.netty.study.util.SessionUtil;

public class LogoutResponseHandle extends SimpleChannelInboundHandler<LogoutResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LogoutResponsePacket logoutResponsePacket){
        SessionUtil.unBindSession(ctx.channel());
    }
}
