package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.concurrent.TimeUnit;

public interface Pipe<IN,OUT> {
    /**
     * 初始化的一部分
     * 设置下一个处理环节  用于将Pipe串联起来
     * */
    void setNextPipe(Pipe<?,?> nextPipe);

    /**
     * 1.初始化
     * */
    void init(PipeContext pipeContext);
    /**
     * 2.执行
     *
     * 处理完后  把处理结果作为下一个处理环节的输入
     * */
    void process(IN input) throws InterruptedException;

    /**
     * 3.结束
     * */
    void shutdown(long timeout, TimeUnit unit);
}
