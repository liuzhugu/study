package org.liuzhugu.javastudy.practice.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {

    /**
     * 暴露服务
     *
     * @param service 实际的服务实现
     * @param port   服务端口
     * @exception Exception
     * */
    public static void export(final Object service,int port)throws Exception{
        if(service==null){
            throw new IllegalArgumentException("service instance==null");
        }
        if(port<=0||port>65535){
            throw new IllegalArgumentException("Invalid port "+port);
        }
        System.out.println("Export service:"+service.getClass().getName()+" on port:"+port);
        ServerSocket server=new ServerSocket(port);
        while(true){
            try {
                final Socket socket=server.accept();
                //新建线程来执行,后期优化可以使用线程池
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                ObjectInputStream input=new ObjectInputStream(socket.getInputStream());
                                try {
                                    //开始处理

                                    //方法   参数  返回值
                                    String methodName=input.readUTF();
                                    Class<?>[] paramterTypes=(Class<?>[])input.readObject();
                                    Object[] param=(Object[]) input.readObject();
                                    ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());

                                    try {
                                        //执行方法
                                        Method method=service.getClass().getMethod(methodName,paramterTypes);
                                        Object result=method.invoke(service,param);
                                        //LOG
                                        System.out.println("ip:"+socket.getInetAddress()+" transfer method:"+methodName+" result:"+result);
                                        //返回结果
                                        out.writeObject(result);
                                    }catch (Throwable t){
                                        //把异常信息返回给调用方
                                        out.writeObject(t);
                                    }finally {
                                        out.close();
                                    }
                                }finally {
                                    input.close();
                                }
                            }finally {
                                socket.close();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass,final String host,final int port)throws Exception{
        if(interfaceClass==null){
            throw new IllegalArgumentException("Interface class == null");
        }
        if(!interfaceClass.isInterface()){
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        }
        if(host==null||host.length()==0){
            throw new IllegalArgumentException("Host == null!");
        }
        if(port<=0||port>65535){
            throw new IllegalArgumentException("Invalid port "+port);
        }
        System.out.println("Get remote service:"+interfaceClass.getName()+" from server "+host+":"+port);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket=new Socket(host,port);
                try {
                    ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                    try {
                        out.writeUTF(method.getName());
                        out.writeObject(method.getParameterTypes());
                        out.writeObject(args);
                        ObjectInputStream input=new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result=input.readObject();
                            if(result instanceof Throwable){
                                throw (Throwable)result;
                            }
                            return result;
                        }finally {
                            input.close();
                        }
                    }finally {
                        out.close();
                    }
                }finally {
                    socket.close();
                }
            }
        });
    }
}
