package org.liuzhugu.javastudy.practice.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println(new Date() +":客户端写出数据");

        // 1. 获取数据
        ByteBuf byteBuf=getByteBuf(ctx);

        //2. 写数据
        ctx.channel().writeAndFlush(byteBuf);

    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx){
        // 1. 获取二进制抽象 ByteBuf
        ByteBuf byteBuf=ctx.alloc().buffer();

        //2. 准备数据，指定字符串的字符集为 utf-8
        byte[] bytes="你好,liuzhugu!".getBytes(Charset.forName("utf-8"));

        //3. 填充数据到 ByteBuf
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }
}
