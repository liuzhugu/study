package org.liuzhugu.javastudy.practice.netty.study.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.LOGOUT_REQUEST;

@Data
public class LogoutRequestPacket extends Packet {

    private int userId;

    @Override
    public Byte getCommand(){
        return LOGOUT_REQUEST;
    }
}
