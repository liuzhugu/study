package org.liuzhugu.javastudy.practice.netty.study.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;
import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.MESSAGE_REQUEST;

@Data
public class MessageRequestPacket extends Packet{

    private int toUserId;

    private String message;

    private String timeStamp;

    public MessageRequestPacket(){}

    public MessageRequestPacket(int toUserId, String message, String timeStamp) {
        this.toUserId = toUserId;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    @Override
    public Byte getCommand(){
        return MESSAGE_REQUEST;
    }
}
