package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AbstractTerminationThread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于工作者线程的Pipe实现类  提交到Pipe的任务由指定个数的工作者线程共同处理   该类使用了两阶段终止
 * @param <IN>
 * @param <OUT>
 * */
public class WorkerThreadPipeDecorator<IN,OUT> implements Pipe<IN,OUT> {
    protected final BlockingQueue<IN> workQueue;
    private final Set<AbstractTerminationThread> workThreads = new HashSet<>();
    private final TerminationToken terminationToken = new TerminationToken();

    private Pipe<IN,OUT> delegate;

    public WorkerThreadPipeDecorator(Pipe<IN, OUT> delegate,int workCount) {
        this(new SynchronousQueue<IN>(),delegate,workCount);
    }

    public WorkerThreadPipeDecorator(BlockingQueue<IN> workQueue, Pipe<IN, OUT> delegate,int workCount) {
        if (workCount <= 0) {
            throw new IllegalArgumentException(
                    "workerCount should be positive!");
        }

        this.workQueue = workQueue;

        this.delegate = delegate;
        for (int i = 0;i < workCount;i ++) {
            workThreads.add(new AbstractTerminationThread() {
                @Override
                protected void doRun() throws Exception {
                    try {
                        //工作线程通过两阶段终止来实现  回调执行doRun方法
                        dispatch();
                    } finally {
                        terminationToken.reservations.decrementAndGet();
                    }
                }
            });
        }
    }

    /**
     * 从工作队列中获取输入的信息  执行当前pipe的业务逻辑
     *
     * @throws InterruptedException
     * */
    protected void dispatch() throws InterruptedException {
        IN input = workQueue.take();
        delegate.process(input);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void init(PipeContext pipeCtx) {
        delegate.init(pipeCtx);
        //启动工作线程
        for (AbstractTerminationThread thread : workThreads) {
            thread.start();
        }
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        for (AbstractTerminationThread thread : workThreads) {
            thread.terminate();
            try {
                thread.join(TimeUnit.MILLISECONDS.convert(timeout,unit));
            } catch (InterruptedException e) {
                ;
            }
        }
        delegate.shutdown(timeout,unit);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        //输入放到work的队列中去
        workQueue.put(input);
        terminationToken.reservations.incrementAndGet();
    }
}
