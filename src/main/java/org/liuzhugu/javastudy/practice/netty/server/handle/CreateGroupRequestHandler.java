package org.liuzhugu.javastudy.practice.netty.server.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import org.liuzhugu.javastudy.practice.netty.common.IdTypeEnum;
import org.liuzhugu.javastudy.practice.netty.protocol.request.CreateGroupRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.CreateGroupResponsePacket;
import org.liuzhugu.javastudy.practice.netty.util.DistributedIdUtil;
import org.liuzhugu.javastudy.practice.netty.util.SessionUtil;


import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {

    @Override
    //收到信息   然后转发
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket createGroupRequestPacket){
        //1.拿到将要创建的群组的userIds
        List<Integer> userIds= createGroupRequestPacket.getUserIds();

        //2.创建一个 channel 分组
        ChannelGroup channelGroup=new DefaultChannelGroup(ctx.executor());

        List<String> userNameList = new ArrayList<>();

        //3.筛选出待加入群聊的用户的 channel 和 userName
        for(int userId:userIds){
            Channel channel = SessionUtil.getChannel(userId);
            if(channel!=null){
                channelGroup.add(channel);
                userNameList.add(SessionUtil.getSession(channel).getUserName());
            }
        }

        //4.创建群组创建结果的响应
        CreateGroupResponsePacket createGroupResponsePacket=new CreateGroupResponsePacket();
        createGroupResponsePacket.setSuccess(true);
        createGroupResponsePacket.setGroupId(DistributedIdUtil.getId(IdTypeEnum.groupId));
        createGroupResponsePacket.setUserNameList(userNameList);

        //5.给每个客户端发送拉群通知
        channelGroup.writeAndFlush(createGroupResponsePacket);

        System.out.print("群创建成功，id 为[" + createGroupResponsePacket.getGroupId() + "], ");
        System.out.println("群里面有：" + createGroupResponsePacket.getUserNameList());
    }
}
