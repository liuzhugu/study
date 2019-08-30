package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.common.IdTypeEnum;
import org.liuzhugu.javastudy.practice.netty.common.Session;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LoginRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.DistributedIdUtil;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LoginRequestPacket loginRequestPacket){
        LoginResponsePacket loginResponsePacket=new LoginResponsePacket();
        int userId=randomUserId();
        loginResponsePacket.setUserId(userId);
        loginResponsePacket.setUserName(loginRequestPacket.getUsername());
        loginResponsePacket.setSuccess(true);
        SessionUtil.bindSession(new Session(userId,loginRequestPacket.getUsername()),ctx.channel());

        //登录响应
        ctx.channel().writeAndFlush(loginResponsePacket);
    }

    private int randomUserId(){
        return DistributedIdUtil.getId(IdTypeEnum.userId);
    }

    // 用户断线之后取消绑定
    public void channelInactive(ChannelHandlerContext ctx){
        SessionUtil.unBindSession(ctx.channel());
    }
}
