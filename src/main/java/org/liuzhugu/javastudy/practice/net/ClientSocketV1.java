package org.liuzhugu.javastudy.practice.net;

import java.io.OutputStream;
import java.net.Socket;

public class ClientSocketV1 {
    private static final int BYTE_LENGTH = 1024;

    public static void main(String[] args) throws Exception{
        //创建socket 与服务器连接
        Socket socket = new Socket("127.0.0.1",9999);
        //准备发送的内容
        final String message = "hello world";
        //使用输出流进行发送
        try (OutputStream outputStream = socket.getOutputStream()) {
            //将信息装入定长缓冲区  再发送
            byte[] bytes = new byte[BYTE_LENGTH];
            int index = 0;
            for (byte b : message.getBytes()) {
                bytes[index ++] = b;
            }
            //发送十次
            for (int i = 0; i < 10; i ++) {
                outputStream.write(bytes,0,BYTE_LENGTH);
            }
        }
    }
}
