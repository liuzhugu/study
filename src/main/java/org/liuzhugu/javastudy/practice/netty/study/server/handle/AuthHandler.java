package org.liuzhugu.javastudy.practice.netty.study.server.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.liuzhugu.javastudy.practice.netty.study.util.SessionUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    @Override
    public  void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        if (!SessionUtil.hasLogin(ctx.channel())) {
            ctx.channel().close();
        } else {
            //第一次时做校验，如果通过了校验，那么对于该channel，后续的链接都可以去掉这个handle
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }

    @Override
    //打印信息，帮助理解
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (SessionUtil.hasLogin(ctx.channel())) {
            System.out.println("当前连接登录验证完毕，无需再次验证, AuthHandler 被移除");
        } else {
            System.out.println("无登录验证，强制关闭连接!");
        }
    }
}
