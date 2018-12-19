package org.liuzhugu.javastudy.practice.netty.study.client.console;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleCommandManager implements ConsoleCommand {

    private static Map<String,ConsoleCommand> consoleCommandMap = new HashMap<>();

    static{
        consoleCommandMap.put("sendToUser", new SendToUserConsoleCommand());
        consoleCommandMap.put("logout", new LogoutConsoleCommand());
        consoleCommandMap.put("createGroup", new CreateGroupConsoleCommand());    }

    @Override
    public void exec(Scanner scanner, Channel channel) {
        //1.获取第一个指令
        String command = scanner.next();

        //2.获取处理实体
        ConsoleCommand consoleCommand = consoleCommandMap.get(command);

        //3.执行命令
        if(consoleCommand!=null){
            consoleCommand.exec(scanner,channel);
        }else {
            System.err.println("无法识别[" + command + "]指令，请重新输入!");
        }
    }
}
