package org.liuzhugu.javastudy.practice.net;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器端 只负责接收消息
 * */
public class ServSocket {
    private static final int BYTE_LENGTH = 20;
    public static void main(String[] args) throws Exception{
        //创建服务器
        ServerSocket serverSocket = new ServerSocket(9999);
        //获取连接
        Socket clientSocker = serverSocket.accept();
        //获取输入流
        try (InputStream inputStream = clientSocker.getInputStream()){
            while (true) {
                //读取输入
                byte[] bytes = new byte[BYTE_LENGTH];
                //处理
                int count = inputStream.read(bytes);
                if (count > 0) {
                    System.out.println("接收到客户端的消息是:" + new String(bytes));
                }
                count = 0;
            }
        }
    }
}
