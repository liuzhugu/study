package org.liuzhugu.javastudy.practice.netty.study.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;

import java.util.List;

import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.CREATE_GROUP_REQUEST;
@Data
public class CreateGroupRequestPacket extends Packet {

    private List<Integer> userIds ;

    @Override
    public Byte getCommand(){
        return CREATE_GROUP_REQUEST;
    }
}
