package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.util.concurrent.*;

/**
 * 基于线程池的Pipe实现类
 * */
public class ThreadPoolPipeDecorator<IN,OUT> implements Pipe<IN,OUT> {

    private final Pipe<IN,OUT> delegate;

    private final ExecutorService executorService;

    /**
     * 线程池停止标志
     * 每个线程池应该有自己的停止管控 方便终结
     * */
    private final CustomTerminationToken customTerminationToken;

    private final CountDownLatch stageProcessDoneLatch = new CountDownLatch(1);

    public ThreadPoolPipeDecorator(Pipe<IN, OUT> delegate, ExecutorService executorService) {
        this.delegate = delegate;
        this.executorService = executorService;
        this.customTerminationToken = CustomTerminationToken.newInstance(executorService);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void init(PipeContext pipeCtx) {
        delegate.init(pipeCtx);
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        customTerminationToken.setToShutdown();
        if (customTerminationToken.reservations.get() > 0) {
            try {
                if (stageProcessDoneLatch.getCount() > 0) {
                    stageProcessDoneLatch.await(timeout,unit);
                }
            } catch (InterruptedException e) {
                ;
            }
        }

        delegate.shutdown(timeout,unit);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                int remainingReservations = -1;
                try {
                    delegate.process(input);
                } catch (InterruptedException e) {
                    ;
                } finally {
                    remainingReservations = customTerminationToken.reservations.decrementAndGet();
                }
            }
        };

        //提交线程池执行
        executorService.submit(task);
        customTerminationToken.reservations.incrementAndGet();
    }

    /**
     * 线程池停止标志
     * 每个ExecutorService实例对应唯一的一个TerminationToken实例。
     *  这里使用了两阶段中止模式的思想来停止多个Pipe实例所公用的线程池实例
     * */
    private static class CustomTerminationToken extends TerminationToken {
        private final static ConcurrentMap<ExecutorService,CustomTerminationToken>
            INSTANCE_MAP = new ConcurrentSkipListMap<>();

        //私有构造器
        private CustomTerminationToken() {

        }

        void setToShutdown() {
            this.toShutdowm = true;
        }

        static CustomTerminationToken newInstance(ExecutorService executorService) {
            CustomTerminationToken token = INSTANCE_MAP.get(executorService);
            if (null == token) {
                token = new CustomTerminationToken();
                CustomTerminationToken existingToken = INSTANCE_MAP.putIfAbsent(executorService,token);
                if (null != existingToken) {
                    token = existingToken;
                }
            }
            return token;
        }
    }
}
