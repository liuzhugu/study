package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

import java.util.concurrent.Callable;

public interface Blocker {

    /**
     * 在保护条件成立时  执行目标动作
     * 否则阻塞当前线程   直到保护条件成立
     * */
    <V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception;

    /**
     * 更改状态的操作   其call方法的返回值为true时
     * 该方法才会唤醒被暂挂的线程
     */
    void signalAfter(Callable<Boolean> stateOperaion) throws Exception;
}
