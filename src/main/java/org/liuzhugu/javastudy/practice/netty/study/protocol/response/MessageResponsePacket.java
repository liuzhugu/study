package org.liuzhugu.javastudy.practice.netty.study.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.MESSAGE_RESPONSE;

@Data
public class MessageResponsePacket extends Packet {

    private String message;

    @Override
    public Byte getCommand(){
        return MESSAGE_RESPONSE;
    }
}
