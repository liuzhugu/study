package org.liuzhugu.javastudy.practice.netty.study.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.CreateGroupResponsePacket;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.MessageResponsePacket;

public class CreateGroupResponseHandle extends SimpleChannelInboundHandler<CreateGroupResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,CreateGroupResponsePacket createGroupResponsePacket){
        System.out.print("群创建成功，id 为[" + createGroupResponsePacket.getGroupId() + "], ");
        System.out.println("群里面有：" + createGroupResponsePacket.getUserNameList());
    }
}
