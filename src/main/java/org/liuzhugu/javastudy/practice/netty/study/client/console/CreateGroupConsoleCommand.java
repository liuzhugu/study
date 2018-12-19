package org.liuzhugu.javastudy.practice.netty.study.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

public class CreateGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        String command = scanner.next();

        String[] userIds = command.split(",");

        if(userIds!=null&&userIds.length!=0){

        }
    }
}
