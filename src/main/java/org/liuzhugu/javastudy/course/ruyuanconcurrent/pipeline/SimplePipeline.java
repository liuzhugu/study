package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SimplePipeline<T,OUT> extends AbstractPipe<T,OUT> implements Pipeline<T,OUT> {
    /**
     * pipeline存放pipe实例的链表  实际类型为一个WorkThreadPipeDecorator
     * */
    private final Queue<Pipe<?,?>> pipes = new LinkedList<>();

    /**
     * 线程池  只负责执行Pipe的初始化而已
     * */
    private final ExecutorService helperExecutor;

    /**
     * 默认
     * */
    public SimplePipeline() {
        //单线程线程池
        this(Executors.newSingleThreadExecutor(
                //线程工厂
                new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r,"SimplePipeline-Helper");
                t.setDaemon(true);
                return t;
            }
        }));
    }

    public SimplePipeline(final ExecutorService helperExecutor) {
        super();
        this.helperExecutor = helperExecutor;
    }

    @Override
    public void addPipe(Pipe<?, ?> pipe) {
        //往pipes里添加一个pipe实例
        pipes.add(pipe);
    }

    public  <IN,OUT> void addAsWorkerThreadBasePipe(Pipe<IN,OUT> delegate,int workCount) {
        //将pipe封装成一个WorkerThreadPipeDecorator,往pipeline中添加一个pipe实例
        addPipe(new WorkerThreadPipeDecorator<>(delegate,workCount));
    }

    public  <IN,OUT> void addAsThreadPoolBasePipe(Pipe<IN,OUT> delegate,ExecutorService executorService) {
        //将pipe封装成一个WorkerThreadPipeDecorator,往pipeline中添加一个pipe实例
        addPipe(new ThreadPoolPipeDecorator<>(delegate,executorService));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    @Override
    public void init(final PipeContext ctx) {
        LinkedList<Pipe<?,?>> pipeList = (LinkedList<Pipe<?,?>>) pipes;
        Pipe<?,?> prePipe = this;
        //初始化pipe  根据pipe的先后顺序进行串联
        for(Pipe<?,?> pipe : pipeList) {
            prePipe.setNextPipe(pipe);
            prePipe = pipe;
        }

        //交给线程池来执行  pipeline封装的task任务
        helperExecutor.submit(new PipeInitTask(ctx,(List) pipes));
    }

    /**
     * pipe初始化任务
     */
    static class PipeInitTask implements Runnable {
        final List<Pipe<?,?>> pipes;
        final PipeContext ctx;

        public PipeInitTask( PipeContext ctx,List<Pipe<?, ?>> pipes) {
            this.pipes = pipes;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                for (Pipe<?,?> pipe : pipes) {
                    //进行每个pipe的初始化
                    pipe.init(ctx);
                }
            } catch (Exception e) {
                System.out.println("Failed to init pipe" + e);
            }
        }
    }

    public PipeContext newDefaultPipelineContext() {
        return new PipeContext() {
            @Override
            public void handleError(PipeException exp) {
                helperExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(exp);
                    }
                });
            }
        };
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        Pipe<?,?> pipe;
        while (null != (pipe = pipes.poll())) {
            //如果任务还没处理完  会阻塞
            pipe.shutdown(timeout,unit);
        }
        helperExecutor.shutdown();
    }

    @Override
    public void process(T input) throws InterruptedException {
        @SuppressWarnings("unchecked")
        Pipe<T, ?> firstPipe = (Pipe<T, ?>) pipes.peek();
        //因为已经串联好Pipe链 因此交由第一个Pipe开始处理  其处理完会自动传给下一个
        firstPipe.process(input);
    }

    @Override
    protected OUT doProcess(T input) throws PipeException {
        //什么也不做
        return null;
    }
}
