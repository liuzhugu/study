package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LogoutRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LogoutResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;

public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LogoutRequestPacket logoutRequestPacket){
        LogoutResponsePacket logoutResponsePacket=new LogoutResponsePacket();
        logoutResponsePacket.setSuccess(true);
        SessionUtil.unBindSession(ctx.channel());
        //登出响应
        ctx.channel().writeAndFlush(logoutResponsePacket);
    }

}
