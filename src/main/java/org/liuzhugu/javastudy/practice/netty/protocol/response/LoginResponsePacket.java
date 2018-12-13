package org.liuzhugu.javastudy.practice.netty.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.netty.protocol.Packet;
import org.liuzhugu.javastudy.practice.netty.protocol.command.Command;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String reason;


    @Override
    public Byte getCommand(){
        return Command.LOGIN_RESPONSE;
    }
}
