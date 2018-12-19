package org.liuzhugu.javastudy.practice.netty.study.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.study.protocol.Packet;

//
import static org.liuzhugu.javastudy.practice.netty.study.protocol.command.Command.LOGIN_RESPONSE;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String reason;

    private int userId;

    private String userName;

    @Override
    public Byte getCommand(){
        return LOGIN_RESPONSE;
    }
}
