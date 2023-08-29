package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 *  案例场景:电商平台定时生成静态化页面  定时同步到指定的nginx服务器上去
 * */
public class CommodityDetailSyncTask implements Runnable {

    /**
     * 灵活扩展的地方有两个
     * 1.要处理的任务
     * 2.处理任务的Pipe
     * */
    @Override
    public void run() {
        // 1 创建Pipeline实例  设置生成和上传两个阶段
        SimplePipeline<CommodityInfoTask,String> pipeline = buildPipeLine();
        // 2 初始化Pipeline实例   串联pipe  将pipe的初始化交由线程池执行
        pipeline.init(pipeline.newDefaultPipelineContext());
        // 3 设置Pipeline要处理的任务
        try {
            //3.1 创建商品查询数据源
            CommodityDbData commodityDbDataList = new CommodityDbData();
            //3.2 使用pipeline来处理商品信息
            processCommodityInfos(commodityDbDataList,pipeline);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 4 处理完毕  终止Pipeline
        pipeline.shutdown(360,TimeUnit.SECONDS);
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
        final SimplePipeline<CommodityInfoTask,String> pipeline = new SimplePipeline<>(helperExecutor);

        /**
         * 第一阶段
         *
         * 根据数据库记录生成相应的静态页面写入到文件中AbstractPipe
         * */
        Pipe<CommodityInfoTask,File> commodityInfoStage = generateCommodityInfoStage();
        pipeline.addAsWorkerThreadBasePipe(commodityInfoStage,1);

        /**
         * 第二阶段
         *
         * 将生成的静态化页面传输到nginx上  并行的pipe:AbstractParallelPipe
         */
        Pipe<File,File> transferFileStage = createFileTransferStage();
        pipeline.addAsWorkerThreadBasePipe(transferFileStage,1);

        return pipeline;
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

        //
        final ThreadPoolExecutor ftpExecutorService = new ThreadPoolExecutor(
                1,
                ftpServerConfigs.length,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100)
        );

        //AbstractParallelPipe类
        ret = new AbstractParallelPipe<File,File, File>(new SynchronousQueue<>(),ftpExecutorService) {
            /**
             * 连接nginx客户端的凭据对象 根据上传的服务器数量来创建
             * */
            @SuppressWarnings("unchecked")
            final Future<FtpUploader>[] ftpClientUtilHolders = new Future[ftpServerConfigs.length];

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
                ftpExecutorService.shutdown();
                try {
                    ftpExecutorService.awaitTermination(timeout,unit);
                } catch (InterruptedException e) {
                    ;
                }
                for (Future<FtpUploader> ftpClientUtilHolder : ftpClientUtilHolders) {
                    try {
                        //断开连接
                        ftpClientUtilHolder.get().disconnect();
                    } catch (Exception E) {
                        ;
                    }
                }
            }

            @Override
            protected File doProcess(File input) throws PipeException {
                return super.doProcess(input);
            }

            /**
             * 任务分片
             * */
            @Override
            protected List<Callable<File>> buildTasks(final File file) throws Exception {
                //创建一组并发任务  将指定的文件上传到多个FTP服务器上
                List<Callable<File>> tasks = new LinkedList<>();
                for (Future<FtpUploader> ftpClientUtilHolder : ftpClientUtilHolders) {
                    tasks.add(new FileTransferTask(ftpClientUtilHolder,file));
                }
                return tasks;
            }

            /**
             * 合并结果
             * */
            @Override
            protected File combineResults(List<Future<File>> subTaskResults) throws Exception {
                if (0 == subTaskResults.size()) {
                    return null;
                }
                //组合执行的结果  这里因为ftp的client只有一个 同步阻塞等待其中一个执行完毕即可
                //然后返回对应的文件数据
                return subTaskResults.get(0).get();
            }
        };
        return ret;
    }

    private void processCommodityInfos(CommodityDbData commodityDbData,
                                       Pipeline<CommodityInfoTask,String> pipeline) throws Exception {
        CommodityInfo commodityInfo;
        while (commodityDbData.hasNext()) {
            commodityInfo = commodityDbData.next();
            //通过pipeline来处理每一个商品任务
            pipeline.process(new CommodityInfoTask(commodityInfo,commodityInfo.getId() + ".html"));
        }
    }
}
