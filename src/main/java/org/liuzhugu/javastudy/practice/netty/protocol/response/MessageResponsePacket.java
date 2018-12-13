package org.liuzhugu.javastudy.practice.netty.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;
import org.liuzhugu.javastudy.practice.netty.protocol.command.Command;

@Data
public class MessageResponsePacket extends Packet {

    private String message;

    @Override
    public Byte getCommand(){
        return Command.MESSAGE_RESPONSE;
    }
}
