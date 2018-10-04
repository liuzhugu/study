package org.liuzhugu.javastudy.practice.nettystudy.study.protocol.response;

import lombok.Data;
import org.liuzhugu.javastudy.practice.nettystudy.study.protocol.Packet;

//
import static org.liuzhugu.javastudy.practice.nettystudy.study.protocol.command.Command.LOGIN_RESPONSE;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String reason;


    @Override
    public Byte getCommand(){
        return LOGIN_RESPONSE;
    }
}
