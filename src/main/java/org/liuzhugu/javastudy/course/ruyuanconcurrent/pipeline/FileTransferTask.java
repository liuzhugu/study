package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.FileInfo;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FileTransferTask implements Callable<File> {
    public final Future<FtpUploader> ftpUtilHolder;
    public final File file2Transfer;

    public FileTransferTask(Future<FtpUploader> ftpUtilHolder, File file2Transfer) {
        this.ftpUtilHolder = ftpUtilHolder;
        this.file2Transfer = file2Transfer;
    }

    @Override
    public File call() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file2Transfer.getName());
        //上次指定文件
        ftpUtilHolder.get().upload(fileInfo);
        return file2Transfer;
    }
}
