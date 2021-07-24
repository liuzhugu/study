package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;

import java.io.IOException;

public class DbRequestPersistence implements RequestPersistence{
    @Override
    public void store(ArticleAccessInfo articleAccessInfo) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("写请求:" + articleAccessInfo + " 到数据库完成");
    }

    @Override
    public void close() throws IOException {
        //什么都不做
    }
}
