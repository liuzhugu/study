package org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable;

public class CustomerCenterThread extends Thread{
    @Override
    public void run() {
        /**
         * 路由信息是否改变
         */
        boolean isChangeRouteInfo = false;

        while (true) {
            /**
             * 读取与客服中心连接的socketChannel中的数据,然后判断是否发生了改变
             * 因为短信中心的路由信息是很少进行改变的
             *
             */
            if (isChangeRouteInfo) {
                // 如果改变了 重置短信网关
                SmsRouter.setInstance(new SmsRouter());
            }
        }
    }
}
