package org.liuzhugu.javastudy.practice.rpc.complex.network.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.liuzhugu.javastudy.practice.rpc.complex.network.future.SyncWriteFuture;
import org.liuzhugu.javastudy.practice.rpc.complex.network.future.SyncWriteMap;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Response;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //返回时  根据请求ID找到相应的Response
        Response response = (Response) msg;
        SyncWriteFuture future = (SyncWriteFuture)SyncWriteMap.syncKey.get(response.getRequestId());
        if (response != null) {
            future.setResponse(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
