package org.liuzhugu.javastudy.course.ruyuanconcurrent.dataanalysissystem;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AbstractTerminationThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ExportWorkThread extends AbstractTerminationThread {

    /**
     * 任务队列
     * */
    private final BlockingQueue<String> workQueue;

    /**
     * 下载excel文件的client
     * */
    private final DownloadExcelClient downloadExcelClient;

    public ExportWorkThread() {
        this.workQueue = new ArrayBlockingQueue<>(1000);
        downloadExcelClient = new DownloadExcelClient();
        downloadExcelClient.init();
    }

    @Override
    protected void doRun() throws Exception {
        String fileName = workQueue.take();
        try {
            downloadExcelClient.download(fileName);
        } finally {
            terminationToken.reservations.decrementAndGet();
        }
    }

    /**
     * 导出文件
     *
     * @param fileName 导出excel文件名称
     * */
    public void exportFile(String fileName) {
        try {
            workQueue.put(fileName);
            terminationToken.reservations.incrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
