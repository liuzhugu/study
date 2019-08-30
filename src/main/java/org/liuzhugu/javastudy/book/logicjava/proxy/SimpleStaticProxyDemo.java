package org.liuzhugu.javastudy.book.logicjava.proxy;

import org.liuzhugu.javastudy.book.logicjava.annotation.Liuzhuzhu;

@Liuzhuzhu(info = "静态代理")
public class SimpleStaticProxyDemo {
    //接口
    static interface IService {
        void action();
    }
    //具体实现1
    static class RealServiceFirst implements IService {
        public void action() {
            System.out.println("this is RealService1!");
        }
    }
    //具体实现2
    static class RealServiceSecond implements IService {
        public void action() {
            System.out.println("this is RealService2!");
        }
    }
    //代理类,代理类因为要与被代理的类保持外观一致,因此也要实现接口
    static class TraceProxy implements IService{

        IService realService;
        public TraceProxy(IService realService) {
            this.realService = realService;
        }

        public void setRealService(IService realService) {
            this.realService = realService;
        }

        @Override
        public void action() {
            System.out.println("enter action");
            this.realService.action();
            System.out.println("leaving action");
        }
    }

    //使用
    public static void main(String[] args) {
        //创建真正工作的类
        IService realService = new RealServiceFirst();
        //创建代理类  面向客户端
        IService traceService = new TraceProxy(realService);
        traceService.action();

        //更换代理类
        System.out.println("替换实现类");
        IService replaceService = new RealServiceSecond();
        ((TraceProxy) traceService).setRealService(replaceService);
        traceService.action();
    }
}
