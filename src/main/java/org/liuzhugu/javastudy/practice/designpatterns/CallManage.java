package org.liuzhugu.javastudy.practice.designpatterns;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 管理任务   发现相同目的的任务已经有线程在执行了  那么其他线程等待最终结果就行
 * */
public class CallManage {
    private final Lock lock = new ReentrantLock();
    private Map<String,Call> callMap;

    //func传进来的是要执行的任务
    public byte[] run(String key, Supplier<byte[]> func) {
        //上锁
        this.lock.lock();
        if (this.callMap == null) {
            this.callMap = new HashMap<>();
        }
        //判断是否该任务已经有线程在执行了
        Call call = this.callMap.get(key);
        //如果已经有了  那么阻塞等待其完成  然后获取结果
        if (call !=  null) {
            //释放锁 让线程执行任务  因为只是简单的判断  所以速度很快
            this.lock.unlock();
            //阻塞等待完成
            call.await();
            //任务完成  获取结果返回
            return call.getVal();
        }
        //没有线程执行该任务  那么去执行
        call = new Call();
        call.lock();
        //告诉后面线程该任务有线程在做了  阻塞等待结果
        this.callMap.put(key,call);
        this.lock.unlock();

        //执行任务   设置结果
        call.setVal(func.get());
        //通知阻塞的线程获取结果
        call.done();

        //该任务完成  因此移除
        this.lock.lock();
        this.callMap.remove(key);
        this.lock.unlock();

        //返回结果
        return call.getVal();
    }
}
