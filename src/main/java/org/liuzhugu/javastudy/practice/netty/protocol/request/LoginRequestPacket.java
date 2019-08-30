package org.liuzhugu.javastudy.practice.netty.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;

import static org.liuzhugu.javastudy.practice.netty.protocol.command.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {

    private int userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand(){
        return LOGIN_REQUEST;
    }
}
