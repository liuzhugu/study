package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

public class CommodityDetailSyncTask implements Runnable {
    @Override
    public void run() {
        //1.创建处理各个任务的Pipe
            //创建被包装过后的Pipe
                //每个包装类中有多条线程  每条线程都会调用pipe去处理任务
                    //包装类的多条线程可以是单纯的多条线程  也可以是线程池中的多条线程
                    //而pipe内部  可以在内部加线程池来并行执行任务  加快执行速度
        //2.初始化Pipeline
            //串联不同的pipe  组成处理链
            //创建初始化任务  交给线程池异步处理
        //3.执行任务
            //不断创建任务
            //将任务不断交给处理链的第一环处理  其处理完后会自动交给下一环  递推
                //每一环只能跟前后环打交道  每一环都使用同一个上下文来初始化  由这个公共上下文统一管理整个链条
        //4.结束
            //遍历终止每个pipe
                //每个pipe终止时  发现自己还有任务没处理完  那么阻塞  直到任务完成  这样才算安全退出
    }
}
