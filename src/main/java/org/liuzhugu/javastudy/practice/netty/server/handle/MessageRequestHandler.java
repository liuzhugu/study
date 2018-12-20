package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.common.Session;
import org.liuzhugu.javastudy.practice.netty.protocol.request.MessageRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.MessageResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

    @Override
    //收到信息   然后转发
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket){
        //1.拿到信息发送方的会话信息
        Session session = SessionUtil.getSession(ctx.channel());

        //2.通过信息发送方的会话信息构造要发送的信息
        MessageResponsePacket messageResponsePacket=new MessageResponsePacket();
        messageResponsePacket.setFromUserId(session.getUserId());
        messageResponsePacket.setFromUserName(session.getUserName());
        messageResponsePacket.setMessage(messageRequestPacket.getMessage());
        messageResponsePacket.setTimeStamp(messageRequestPacket.getTimeStamp());

        //3.拿到信息接收方的channel
        Channel toUserChannel = SessionUtil.getChannel(messageRequestPacket.getToUserId());

        //4.将信息转发给信息接收方
        if(toUserChannel!=null&&SessionUtil.hasLogin(toUserChannel)){
            toUserChannel.writeAndFlush(messageResponsePacket);
        }else {
            System.out.println("[" + messageRequestPacket.getToUserId() + "] 不在线，发送失败!");
        }
    }
}
