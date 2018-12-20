package org.liuzhugu.javastudy.practice.netty.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LogoutResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;

public class LogoutResponseHandle extends SimpleChannelInboundHandler<LogoutResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LogoutResponsePacket logoutResponsePacket){
        SessionUtil.unBindSession(ctx.channel());
    }
}
