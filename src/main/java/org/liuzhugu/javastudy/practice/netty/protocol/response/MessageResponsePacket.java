package org.liuzhugu.javastudy.practice.netty.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.protocol.command.Command.MESSAGE_RESPONSE;

@Data
public class MessageResponsePacket extends Packet {

    private int fromUserId;

    private String fromUserName;

    private String message;

    private String timeStamp;

    @Override
    public Byte getCommand(){
        return MESSAGE_RESPONSE;
    }
}
