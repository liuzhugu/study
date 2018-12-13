package org.liuzhugu.javastudy.practice.netty.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;
import org.liuzhugu.javastudy.practice.netty.protocol.command.Command;

@Data
public class MessageRequestPacket extends Packet {

    private String message;

    public MessageRequestPacket(String message) {
        this.message = message;
    }

    @Override
    public Byte getCommand(){
        return Command.MESSAGE_REQUEST;
    }
}
