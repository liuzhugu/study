package org.liuzhugu.javastudy.practice.netty.study.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.MessageResponsePacket;

import java.util.Date;

public class MessageResponseHandle extends SimpleChannelInboundHandler<MessageResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,MessageResponsePacket messageResponsePacket){
        int fromUserId = messageResponsePacket.getFromUserId();
        String fromUserName = messageResponsePacket.getFromUserName();
        String timeStamp = messageResponsePacket.getTimeStamp();
        String message = messageResponsePacket.getMessage();

        System.out.println(fromUserId+" "+fromUserName+" "+timeStamp+" :"+message);

    }
}
