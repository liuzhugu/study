package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;


import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 异步建立网络连接
 * */
public class CloudUploaderPromisor {

    /**
     * 通过云盘上传的promisor组件构建的一个Promise
     * @param cloudSyncConfig  云盘同步时的配置
     * @return 云盘同步时的promise凭据对象
     * */
    public static Future<CloudUploader> newCloudUploaderPromise(CloudSyncConfig cloudSyncConfig) {
        FutureTask<CloudUploader> futureTask = new FutureTask<CloudUploader>(() -> {
            DefaultCloudUploader defaultCloudUploader = new DefaultCloudUploader();
            System.out.println("开始创建云盘的连接");
            defaultCloudUploader.init(cloudSyncConfig.getCloudAddress(),
                                        cloudSyncConfig.getUsername(),
                                        cloudSyncConfig.getPassword(),
                                        cloudSyncConfig.getServerDir());
            return defaultCloudUploader;
        });

        //这里模拟一下后台线程去初始化client，正常是线程池模式
        new Thread(futureTask).start();

        return futureTask;
    }
}
