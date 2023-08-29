package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;

import java.io.Closeable;

/**
 * 请求持久化
 * */
public interface RequestPersistence extends Closeable {
    /**
     * 持久化存储访问文章的请求
     *
     * @param articleAccessInfo 访问的文章信息
     */
    void store(ArticleAccessInfo articleAccessInfo);
}
