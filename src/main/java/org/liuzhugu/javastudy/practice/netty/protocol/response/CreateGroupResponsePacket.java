package org.liuzhugu.javastudy.practice.netty.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;

import java.util.List;

import static org.liuzhugu.javastudy.practice.netty.protocol.command.Command.CREATE_GROUP_RESPONSE;
@Data
public class CreateGroupResponsePacket extends Packet{

    private boolean success;

    private int groupId;

    private List<String> userNameList;

    @Override
    public Byte getCommand(){
        return CREATE_GROUP_RESPONSE;
    }
}
