package org.liuzhugu.javastudy.practice.netty.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;
import org.liuzhugu.javastudy.practice.netty.protocol.command.Command;

@Data
public class LoginRequestPacket extends Packet {

    private String userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand(){
        return Command.LOGIN_REQUEST;
    }
}
