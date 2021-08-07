package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 *  案例场景:电商平台定时生成静态化页面  定时同步到指定的nginx服务器上去
 * */
public class CommodityDetailTask implements Runnable {
    @Override
    public void run() {
        // 创建Pipe实例
        SimplePipeline<CommodityDetailTask,String> pipeline =
    }

    /**
     * 创建Pipeline
     *
     * @return 结果
     * */
    private SimplePipeline<CommodityInfoTask,String> buildPipeLine() {
        /**
         * 线程池的本质是重复利用一定数量的线程   而不是针对每一个任务都有一个专门的工作线程
         * 这里 各个pipe的初始化完全可以在上游Pipe初始化完毕后再初始化其后续Pipe  而不必多个Pipe同时初始化
         * 因此  这个初始化的动作可以由一个线程来处理   该线程处理完各个Pipe的初始化后  可以继续处理之后可能产生的
         * 任务  如错误处理
         * 所以上述这些先后产生的任务可以由线程池中的一个工作者线程从头到尾负责执行
         *
         * 由于这里的几个Pipe都是处理I/O的  为了避免使用锁(以减少不必要的上下文切换)但又能保证线程安全  故每个
         * Pipe都才用单线程  若各个pipe要改用线程池来处理  需要注意 1.线程安全  2.死锁
         * */
        final ExecutorService helperExecutor = Executors.newSingleThreadExecutor();
        final SimplePipeline<CommodityDetailTask,String> pipeline = new SimplePipeline<>(helperExecutor);

        /**
         * 根据数据库记录生成相应的静态页面写入到文件中AbstractPipe
         * */
        Pipe<CommodityDetailTask,String> commodityInfoStage =
    }

    private Pipe<CommodityInfoTask, File> generateCommodityInfoStage() {
        Pipe<CommodityInfoTask,File> ret;
        //AbstractPipe类的
        ret = new AbstractPipe<CommodityInfoTask, File>() {
            @Override
            protected File doProcess(CommodityInfoTask task) throws PipeException {
                /**
                 * 将记录写入文件
                 * */
                File file;
                //获取商品详情的模板
                final CommodityDetailTemplate commodityDetailTemplate = CommodityDetailTemplate.getInstance();
                final CommodityInfo commodityInfo = task.commodityInfo;

                try {
                    //根据模板和数据生成静态文件
                    file = commodityDetailTemplate.generate(commodityInfo,task.targetFileName);
                } catch (IOException e) {
                    throw new PipeException(this,task,"Failed to save record",e);
                }
                return file;
            }
        };
        return ret;
    }

    protected Pipe<File,File> createFileTransferStage() {
        Pipe<File,File> ret;
        final String[][] ftpServerConfigs = {{"127.0.0.1"}};

        final ThreadPoolExecutor ftpExecutorService = new ThreadPoolExecutor(
                1,
                ftpServerConfigs.length,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100)
        );

        //AbstractParallelPipe类
        ret = new AbstractParallelPipe<File,File, File>(new SynchronousQueue<>(),ftpExecutorService) {
            @SuppressWarnings("unchecked")
            final Future<FtpUpload>[] ftpClientUtilHolders = new Future[ftpServerConfigs.length];

            @Override
            public void init(PipeContext pipeCtx) {
                super.init(pipeCtx);
                //构建一组子任务  建立ftp client和server端的连接  通过承诺模式实现
                String[] ftpServerConfig;
                for (int i = 0;i < ftpServerConfigs.length;i ++) {
                    ftpServerConfig = ftpServerConfigs[i];
                    //FTPUploaderPromise才用承诺模式
                    ftpClientUtilHolders[i] = FtpUploaderPromisor.newFtpUploaderPromise();
                }
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {
                super.shutdown(timeout, unit);
            }

            @Override
            protected List<Future<File>> invokeParallel(List<Callable<File>> tasks) throws Exception {
                return super.invokeParallel(tasks);
            }

            @Override
            protected File doProcess(File input) throws PipeException {
                return super.doProcess(input);
            }

            @Override
            protected List<Callable<File>> buildTasks(File input) throws Exception {
                return null;
            }

            @Override
            protected File combineResults(List<Future<File>> subTaskResults) throws Exception {
                return null;
            }
        };
        return ret;
    }
}
