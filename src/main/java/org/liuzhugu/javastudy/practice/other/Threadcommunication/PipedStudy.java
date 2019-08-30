package org.liuzhugu.javastudy.practice.other.Threadcommunication;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
/**
 *  管道通信
 * */
public class PipedStudy {

    public static void main(String[] args)throws Exception{
        final PipedWriter writer=new PipedWriter();
        final PipedReader reader=new PipedReader();

        writer.connect(reader);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("开始写");
                try {
                    for(int i=0;i<15;i++){
                        writer.write(i+"");
                        Thread.sleep(100);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        writer.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("写结束");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("开始读");
                int msg = 0;
                try {
                    while((msg=reader.read())!=-1){
                        System.out.println("msg is:"+msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("读结束");
            }
        }).start();
    }
}
