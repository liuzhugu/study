package org.liuzhugu.javastudy.practice.nettystudy.study.protocol.request;

import lombok.Data;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.Packet;

import static org.liuzhugu.javastudy.practice.nettystudy.study.protocol.command.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {

    private String userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand(){
        return LOGIN_REQUEST;
    }
}
