package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbstractParallelPipe<IN,OUT,V> extends AbstractPipe<IN,OUT>{
    private final ExecutorService executorService;

    public AbstractParallelPipe(ExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

    /**
     * 并行执行任务
     * */
    List<Future<V>> invokeParallel(List<Callable<V>> tasks) throws InterruptedException{
        //交由线程池执行
        return executorService.invokeAll(tasks);
    }

    @Override
    public OUT doProcess(IN input) throws PipeException {
        OUT out = null;
        try {
            //构建任务  并行执行  得到结果  交由下一个pipe执行
            out = combineResults(invokeParallel(buildTask(input)));
        } catch (Exception e) {
            throw new PipeException(this,input,"TaskFail",e);
        }
        return out;
    }

    /**
     * 构建任务
     * */
    protected abstract List<Callable<V>> buildTask(IN input) throws Exception;

    /**
     * 获取结果
     * */
    protected abstract OUT combineResults(List<Future<V>> subTaskResults) throws Exception;
}
