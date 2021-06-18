package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    private static Socket socket;

    private int number;

    private static PrintWriter printWriter;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void stop() {
        if (! socket.isClosed()) {
            try {
                doStop();
                socket.close();;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doStop() {
        if (! TerminationToken.getInstance().isRunning()) {
            printWriter.println(String.format("%s号客户你好，本店已打烊，请明天再来",number));
            printWriter.flush();
        }
        System.out.println(String.format("%s号顾客 离开店铺",number));
    }
}
