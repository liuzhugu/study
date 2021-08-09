package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.promise.FileInfo;

/**
 * 默认个人网盘上传的实现类
 * */
public class DefaultFtpUploader implements FtpUploader {

    @Override
    public void init(String cloudServer, String ftpUserName, String password, String serverDir) throws Exception {
        //假设休眠1s
        Thread.sleep(1000);
        System.out.println("与远程个人网盘的连接建立完成");
    }

    @Override
    public void upload(FileInfo fileInfo) throws Exception {
        //上传文件  模拟睡眠100ms
        Thread.sleep(100);
        System.out.println("上传文件:" + fileInfo.getFileName() + " 完成");
    }

    @Override
    public void disconnect() {
        System.out.println("关闭与远程网盘的连接");
    }
}
