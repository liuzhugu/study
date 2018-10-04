package org.liuzhugu.javastudy.practice.nettystudy.study.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.Packet;
import static org.liuzhugu.javastudy.practice.nettystudy.study.protocol.command.Command.MESSAGE_REQUEST;

@Data
public class MessageRequestPacket extends Packet{

    private String message;

    public MessageRequestPacket(String message) {
        this.message = message;
    }

    @Override
    public Byte getCommand(){
        return MESSAGE_REQUEST;
    }
}
