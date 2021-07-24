package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;


import java.util.Collections;
import java.util.List;

/**
 * 单例模式
 * */
//无法继承
public final class StorageManager {

    /**
     *  饿汉式
     * */
    private static final StorageManager INSTANCE = new StorageManager();

    //无法通过构造器创建对象
    private StorageManager() {

    }

    public static StorageManager getInstance() {
        return INSTANCE;
    }

    /**
     * 扫描本地需要同步的文件
     *
     * @return
     */
    public List<FileInfo> scanLocalFile() {
        // 模拟需要同步到个人云盘的文件
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFile("abc".getBytes());
        fileInfo.setFileName("abc.txt");
        fileInfo.setFileSize(fileInfo.getFile().length);
        return Collections.singletonList(fileInfo);
    }}
