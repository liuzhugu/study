package org.liuzhugu.javastudy.practice.netty.study.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.LOGOUT_RESPONSE;


@Data
public class LogoutResponsePacket extends Packet {
    private boolean success;

    private String reason;

    @Override
    public Byte getCommand(){
        return LOGOUT_RESPONSE;
    }
}
