package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.concurrent.*;

/**
 * 线程池实现
 * */
public class ThreadPoolPipeDecorator<IN,OUT> implements Pipe<IN,OUT> {
    /**
     * 被包装的类
     */
    private Pipe<IN,OUT> delegate;
    private ExecutorService executorService;

    /**
     * 两阶段终止
     * */
    private CustomTerminationToken customTerminationToken;
    private final CountDownLatch stageProcessDoneLatch = new CountDownLatch(1);

    public ThreadPoolPipeDecorator(Pipe<IN, OUT> delegate, ExecutorService executorService) {
        this.delegate = delegate;
        this.executorService = executorService;
        this.customTerminationToken = CustomTerminationToken.getInstance(executorService);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void init(PipeContext pipeContext) {
        delegate.init(pipeContext);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        //包装成一个任务
        Runnable task = new Runnable() {
            @Override
            public void run() {
                int remainingRevervations = -1;
                try {
                    delegate.process(input);
                } catch (InterruptedException e) {
                    ;
                } finally {
                    //未完成的任务数
                    remainingRevervations = customTerminationToken.reservations.decrementAndGet();
                }
                //如果
                if (customTerminationToken.isToShutdown() && 0 == remainingRevervations) {
                    stageProcessDoneLatch.countDown();
                }
            }
        };
        //交由线程池执行
        executorService.submit(task);
        customTerminationToken.reservations.incrementAndGet();
    }

    /**
     * 交由二阶段终止来实现
     * */
    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        customTerminationToken.setToShutdown();
        //还有未完成的任务的话
        if (customTerminationToken.reservations.get() > 0) {
            try {
                //等待任务完成 因为上面那个无法等待  只能建个栅栏来等待
                //当任务陆续完成时   最后一个完成的任务会修改栅栏  从而使得这里从等待中唤醒  从而退出
                if (stageProcessDoneLatch.getCount() > 0) {
                    stageProcessDoneLatch.await(timeout, unit);
                }
            } catch (InterruptedException e) {
                ;
            }
        }
        delegate.shutdown(timeout,unit);
    }

    /**
     * 不同线程池对应不同的token
     * */
    private static class CustomTerminationToken extends TerminationToken {
        private final static ConcurrentMap<ExecutorService,CustomTerminationToken>
            INSTANCE_MAP = new ConcurrentSkipListMap<>();

        //私有构造方法
        private CustomTerminationToken() {}

        public static CustomTerminationToken getInstance(ExecutorService executorService) {
            CustomTerminationToken token = INSTANCE_MAP.get(executorService);
            if (null == token) {
                token = new CustomTerminationToken();
                //尝试着设置  如果已经被其他线程设置了  那么就返回已经设置的
                CustomTerminationToken exsitingToken = INSTANCE_MAP.putIfAbsent(executorService,token);
                if (null != exsitingToken) {
                    token = exsitingToken;
                }
            }
            return token;
        }
    }
}
