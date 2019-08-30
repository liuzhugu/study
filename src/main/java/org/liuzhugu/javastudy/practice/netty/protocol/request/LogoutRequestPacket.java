package org.liuzhugu.javastudy.practice.netty.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.protocol.command.Command.LOGOUT_REQUEST;

@Data
public class LogoutRequestPacket extends Packet {

    private int userId;

    @Override
    public Byte getCommand(){
        return LOGOUT_REQUEST;
    }
}
