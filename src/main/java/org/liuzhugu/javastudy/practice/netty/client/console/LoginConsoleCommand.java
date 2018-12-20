package org.liuzhugu.javastudy.practice.netty.client.console;

import io.netty.channel.Channel;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LoginRequestPacket;

import java.util.Scanner;

public class LoginConsoleCommand implements  ConsoleCommand{
    @Override
    public void exec(Scanner scanner, Channel channel) {
        LoginRequestPacket loginRequestPacket=new LoginRequestPacket();

        System.out.print("输入用户名登录: ");
        loginRequestPacket.setUsername(scanner.nextLine());
        loginRequestPacket.setPassword("pwd");

        channel.writeAndFlush(loginRequestPacket);
        waitForLoginResponse();
    }

    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }
}
