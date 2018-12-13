package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LoginRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.LoginUtil;

import java.util.Date;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,LoginRequestPacket loginRequestPacket){
        System.out.println(new Date()+": 收到客户端登录请求……");
        // 登录流程
        LoginResponsePacket loginResponsePacket=new LoginResponsePacket();
        loginResponsePacket.setVersion(loginRequestPacket.getVersion());
        if(valid(loginRequestPacket)){
            //登陆验证过后，标记该channel已登陆
            LoginUtil.markAsLogin(ctx.channel());
            loginResponsePacket.setSuccess(true);
            System.out.println(new Date()+": 登录成功!");
        }else {
            loginResponsePacket.setReason("账号密码校验失败");
            loginResponsePacket.setSuccess(false);
            System.out.println(new Date() + ": 登录失败!");
        }
        //登录响应
        ctx.channel().writeAndFlush(loginResponsePacket);
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }

}
