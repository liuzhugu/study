package org.liuzhugu.javastudy.practice.rpc.complex.config.reflect;

import org.liuzhugu.javastudy.practice.rpc.complex.network.future.SyncWrite;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Request;
import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JDKInvocationHandler implements InvocationHandler {
    private Request request;

    public JDKInvocationHandler(Request request) {
        this.request = request;
    }

    //按照模板生成增强类
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        if ("toString".equals(methodName) && paramTypes.length == 0) {
            return request.toString();
        } else if ("hashcode".equals(methodName) && paramTypes.length == 0){
            return request.hashCode();
        } else if ("equals".equals(methodName) && paramTypes.length == 1) {
            return request.equals(args[0]);
        }
        //设置参数
        request.setMethodName(methodName);
        request.setParameterTypes(paramTypes);
        request.setArgs(args);
        request.setRef(request.getRef());
        Response response = new SyncWrite().writeAndSync(request.getChannel(),request,500);
        //异步调用
        return response.getResult();
    }
}
