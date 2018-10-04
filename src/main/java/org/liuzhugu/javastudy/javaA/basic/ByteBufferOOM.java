package org.liuzhugu.javastudy.javaA.basic;

import java.nio.ByteBuffer;

/**
 * Created by liuting6 on 2018/1/3.
 * 让DirectBuffer内存溢出
 */
public class ByteBufferOOM {
    public static void main(String[] args){
        ByteBuffer.allocateDirect(299*1024*1024);
    }
}
