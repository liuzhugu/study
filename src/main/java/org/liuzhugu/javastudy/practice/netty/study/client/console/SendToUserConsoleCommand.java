package org.liuzhugu.javastudy.practice.netty.study.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

public class SendToUserConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        System.out.println();
    }
}
