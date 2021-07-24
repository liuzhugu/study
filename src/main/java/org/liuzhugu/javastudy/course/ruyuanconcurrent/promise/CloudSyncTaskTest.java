package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

/**
 * 个人本地文件和云盘的同步测试
 * */
public class CloudSyncTaskTest {
    public static void main(String[] args) throws InterruptedException {
        CloudSyncConfig cloudSyncConfig = new CloudSyncConfig("www.baidu.com",
                "ruyuan","ruyuan","/ruyuan");

        CloudSyncTask cloudSyncTask = new CloudSyncTask(cloudSyncConfig);

        // 正常应该是client端一个线程池来和云盘进行数据的同步，这里模拟测试一下
        Thread thread = new Thread(cloudSyncTask);
        thread.start();
        thread.join();
    }
}
