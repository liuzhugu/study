package org.liuzhugu.javastudy.practice.nettystudy.study.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.response.MessageResponsePacket;
import org.liuzhugu.javastudy.practice.nettystudy.study.util.LoginUtil;

import java.util.Date;

public class MessageResponseHandle extends SimpleChannelInboundHandler<MessageResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,MessageResponsePacket messageResponsePacket){
        System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());
    }
}
