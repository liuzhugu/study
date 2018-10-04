package org.liuzhugu.javastudy.practice.nettystudy.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args)throws IOException{

        Selector serverSelector =Selector.open();
        Selector clientSelector =Selector.open();

        new Thread(() -> {
            try {
                //对应IO编程中服务端启动
                ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(1000));
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while(true){
                    // 监测是否有新的连接，这里的1指的是阻塞的时间为 1ms
                    if(serverSelector.select(1)>0){
                        Set<SelectionKey> set=serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator=set.iterator();

                        while(keyIterator.hasNext()){
                            SelectionKey key=keyIterator.next();

                            if(key.isAcceptable()){
                                try {
                                    // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clentChannel=((ServerSocketChannel)key.channel()).accept();
                                    clentChannel.configureBlocking(false);
                                    clentChannel.register(clientSelector,SelectionKey.OP_READ);
                                }finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            }catch (IOException ignored){

            }
        }).start();

        new Thread(() -> {
            try {
                while(true){
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为 1ms
                    if(clientSelector.select(1)>0){
                        Set<SelectionKey> set=clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator=set.iterator();

                        while(keyIterator.hasNext()){
                            SelectionKey key=keyIterator.next();

                            if(key.isReadable()){
                              try {
                                  SocketChannel clentChannel=(SocketChannel)key.channel();
                                  ByteBuffer byteBuffer=ByteBuffer.allocate(2014);
                                  //(3) 读取数据以块为单位批量读取
                                  clentChannel.read(byteBuffer);
                                  byteBuffer.flip();
                                  System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                              }finally {
                                  keyIterator.remove();
                                  key.interestOps(SelectionKey.OP_READ);
                              }
                            }
                        }
                    }
                }
            }catch (IOException  ignored){

            }
        }).start();
    }
}
