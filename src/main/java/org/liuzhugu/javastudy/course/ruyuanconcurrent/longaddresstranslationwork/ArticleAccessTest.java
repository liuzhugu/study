package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;


/**
 * 测试文章的访问
 * */
public class ArticleAccessTest {
    public static void main(String[] args) {
        ArticleAccessHandler articleAccessHandler = new ArticleAccessHandler();
        ArticleAccessInfo articleAccessInfo = new ArticleAccessInfo();
        articleAccessInfo.setOriginIp("127.0.0.1");
        articleAccessInfo.setShortUrl("baidu.com");
        articleAccessHandler.intercept(articleAccessInfo);
    }
}
