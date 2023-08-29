package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.util.concurrent.TimeUnit;

/**
 * 对处理阶段的抽象   负责对其输入进行处理  并将其作为下一处理阶段的输入
 * 阶段1 -> 阶段2
 * 比如输入阶段01 -> 执行完之后阶段1的结果 -> 作为下一个pipe(阶段2)的输入
 * */
public interface Pipe<IN,OUT> {
    /**
     * 设置当前pipe实例的下一个pipe实例
     *
     * @param nextPipe 下一个pipe实例
     * */
    void setNextPipe(Pipe<?,?> nextPipe);

    /**
     * 初始化当前pipe实例对外提供的服务
     *
     * @param pipeCtx  pipe处理上下文
     * */
    void init(PipeContext pipeCtx);

    /**
     * 停止当前pipe实例对外提供的服务
     *
     * @param timeout
     * @param unit
     * */
    void shutdown(long timeout, TimeUnit unit);

    /**
     * 对输入元素进行理   并将处理结果作为下一个pipe实例的输入
     *
     * @param input 输入
     * @throws InterruptedException
     * */
    void process(IN input) throws InterruptedException;
}
