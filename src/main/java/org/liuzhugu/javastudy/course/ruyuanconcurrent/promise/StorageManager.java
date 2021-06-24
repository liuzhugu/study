package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;


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
    protected StorageManager() {

    }

    public static StorageManager getInstance() {
        return INSTANCE;
    }

    public List<FileInfo> scanLocalFile() {
        return null;
    }
}
