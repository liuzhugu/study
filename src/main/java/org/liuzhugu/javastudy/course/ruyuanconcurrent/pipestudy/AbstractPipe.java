package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.concurrent.TimeUnit;

public abstract class AbstractPipe<IN,OUT> implements Pipe<IN,OUT> {
    /**
     * 记录当前pipe的下一个pipe对象
     * */
    private Pipe<?,?> nextPipe;
    /**
     * 整条流水线的上下文  否则每个pipe只能和前后pipe交互
     * */
    private PipeContext pipeContext;

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        //设置当前pipe的下一个pipe   组成调用链
        this.nextPipe = nextPipe;
    }

    @Override
    public void init(PipeContext pipeContext) {
        this.pipeContext = pipeContext;
    }

    @Override
    public void process(IN input) throws InterruptedException {
        try {
            //当前pipe处理输入
            OUT out = doProcess(input);
            if (null != nextPipe) {
                if (null != out) {
                    //将输出作为下一个pipe的出入
                    ((Pipe<OUT,?>)nextPipe).process(out);
                }
            }
        } catch (InterruptedException e) {
            //中断当前线程
            Thread.currentThread().interrupt();
        } catch (PipeException e) {
            //PipeException都交由上下文统一处理
            pipeContext.handleError(e);
        } catch (Exception el) {
            pipeContext.handleError(new PipeException(this,input,"",el));
        }
    }

    /**
     * 交由子类实现
     * */
    protected abstract OUT doProcess(IN input) throws PipeException;

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        //什么也不做
    }
}
