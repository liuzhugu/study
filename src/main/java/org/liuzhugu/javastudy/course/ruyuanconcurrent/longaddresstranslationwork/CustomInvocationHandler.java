package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 主动对象模式
 * */
public class CustomInvocationHandler implements InvocationHandler {

    //真正执行业务逻辑的地方
    private Object target;

    //引入线程池   将任务异步执行
    private ExecutorService scheduler;

    public CustomInvocationHandler(Object target,ExecutorService scheduler) {
        this.target = target;
        this.scheduler = scheduler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //需要返回值  那么使用Callable而不是Runnable
        Callable<Object> methodQuest = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //TODO 执行真正的业务逻辑
                return null;
            }
        };
        //返回一个结果凭证   异步执行之后可以获取结果
        Future<Object> future = scheduler.submit(methodQuest);
        return future;
    }

    public static <T> T newInstance(Class<T> interfaces,ExecutorService scheduler,Servant servant) {
        CustomInvocationHandler handler = new CustomInvocationHandler(servant,scheduler);
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(
                interfaces.getClassLoader(),
                new Class[]{interfaces},
                handler
        );
        return proxy;
    }
}
