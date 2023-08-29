package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SimplePipeline<IN,OUT> extends AbstractPipe<IN,OUT> implements Pipe<IN,OUT> {
    // 该pipeline下的所有pipe实例
    private final Queue<Pipe<?,?>> pipes = new LinkedList<>();
    //线程池  只负责实现初始化任务
    private final ExecutorService helperExecutor;

    public SimplePipeline() {
        this(Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread("SimplePipeline-Helper");
                thread.setDaemon(true);
                return thread;
            }
        }));
    }

    public SimplePipeline(ExecutorService helperExecutor) {
        super();
        this.helperExecutor = helperExecutor;
    }

    @Override
    public void init(PipeContext pipeContext) {
        //串联化pipe

        for(Pipe<?,?> pipe : pipes) {

        }
    }

    @Override
    public void process(IN input) throws InterruptedException {
        super.process(input);
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        super.shutdown(timeout, unit);
    }

    @Override
    protected OUT doProcess(IN input) throws PipeException {
        return null;
    }
}
