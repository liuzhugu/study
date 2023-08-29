package org.liuzhugu.javastudy.course.ruyuanconcurrent.waitandnotify;

import org.apache.ibatis.annotations.Param;
import org.liuzhugu.javastudy.book.springinaction.chapter1.Quest;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ArrayBlockingQueue_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Condition_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ReentrantLock_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.queue.Queue_;

import java.util.concurrent.locks.ReentrantLock;

public class RequestQueue {
    private static final int MAX_LIMIT = 0;

    private static final int LIMIT = 0;

    private Queue_<Request> queue = new ArrayBlockingQueue_<>(MAX_LIMIT);

    ReentrantLock_ lock = new ReentrantLock_();

    Condition_ condition  = lock.newCondition();

    public Request get() {
        Request result = null;
        lock.lock();
        try {
            while (queue.isEmpty()) {
                //挂起   被唤醒后  判断队列是否为空  为空继续挂起   直到队列不为空
                condition.await();
            }
            result = queue.poll();
            //唤醒
            condition.signalAll();
        } catch (InterruptedException e) {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void put(Request request) {
        lock.lock();
        try {
            while (queue.size() >= LIMIT) {
                condition.wait();
            }
            queue.offer(request);
            condition.signalAll();
        } catch (InterruptedException e) {
            condition.signalAll();
        } finally {
            lock.unlock();
        }

    }

}
