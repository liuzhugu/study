package org.liuzhugu.javastudy.practice.rpc.complex.network.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Request;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Response;
import org.liuzhugu.javastudy.practice.rpc.complex.util.ClassLoaderUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private  ApplicationContext applicationContext;

    public ServerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       try {
           //根据请求 找到相应方法处理
           Request request = (Request) msg;
           //调用
           Class<?> classType = ClassLoaderUtils.forName(request.getNozzle());
           Method addMthod = classType.getMethod(request.getMethodName(),request.getParameterTypes());
           Object objectBean = applicationContext.getBean(request.getRef());
           Object result = addMthod.invoke(objectBean,request.getArgs());
           //反馈
           Response response = new Response();
           response.setRequestId(request.getRequestId());
           response.setResult(result);
           ctx.writeAndFlush(response);
           //释放
           ReferenceCountUtil.release(response);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
