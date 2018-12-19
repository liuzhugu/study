package org.liuzhugu.javastudy.practice.netty.study.client.console;

import io.netty.channel.Channel;
import org.liuzhugu.javastudy.practice.netty.study.protocol.request.LogoutRequestPacket;
import org.liuzhugu.javastudy.practice.netty.study.protocol.response.LogoutResponsePacket;

import java.util.Scanner;

public class LogoutConsoleCommand implements ConsoleCommand{

    @Override
    public void exec(Scanner scanner, Channel channel) {
        String userId = scanner.nextLine();
        LogoutRequestPacket logoutRequestPacket=new LogoutRequestPacket();
        logoutRequestPacket.setUserId(Integer.parseInt(userId));
        channel.writeAndFlush(logoutRequestPacket);
    }
}
