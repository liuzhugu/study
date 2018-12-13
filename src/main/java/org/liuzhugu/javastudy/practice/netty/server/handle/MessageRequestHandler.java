package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.protocol.request.MessageRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.MessageResponsePacket;

import java.util.Date;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket){
        // 客户端发来消息
        MessageResponsePacket messageResponsePacket=new MessageResponsePacket();
        System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());
        messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
        ctx.channel().writeAndFlush(messageResponsePacket);
    }
}
