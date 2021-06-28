package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Future_;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class CloudClient {
    public static void main(String[] args) {
        //1.初始化创建本地client和server端的ftp连接
        //因为与云盘建立网络连接是很慢的过程   这里异步化地去执行  云盘任务继续做其他
        CloudSyncConfig cloudSyncConfig = new CloudSyncConfig("","","","");
        Future_<CloudUploader> cloudUploaderFuture = CloudUploaderPromisor.newCloudUploaderPromise(cloudSyncConfig);

        //2.扫描本地的文件
        StorageManager storageManager = StorageManager.getInstance();
        List<FileInfo> fileInfos = storageManager.scanLocalFile();
        System.out.println("扫描本地需要同步的文件完成");

        //3.获取云盘初始化的client
        CloudUploader cloudUploader = null;
        try {
            System.out.println("同步阻塞等待云盘连接建立进行文件上传");
            cloudUploader = cloudUploaderFuture.get();
            System.out.println("获取云盘连接成功");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (cloudUploader == null) {
            return;
        }

        //4.开始同步文件到云盘上去
        syncFile(cloudUploader,fileInfos);
        System.out.println("所有文件上传完成");

        //5.都上传完成，关闭连接
        cloudUploader.discount();
        System.out.println("关闭云盘连接");
    }

    private static void syncFile(CloudUploader cloudUploader,List<FileInfo> fileInfos) {

    }
}
