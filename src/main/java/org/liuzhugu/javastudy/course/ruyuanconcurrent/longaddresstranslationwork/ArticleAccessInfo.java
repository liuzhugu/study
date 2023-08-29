package org.liuzhugu.javastudy.course.ruyuanconcurrent.longaddresstranslationwork;

public class ArticleAccessInfo {

    /**
     * 请求的短网址
     */
    private String shortUrl;

    /**
     * 请求的来源的ip
     */
    private String originIp;

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    @Override
    public String toString() {
        return "ArticleAccessInfo{" +
                "shortUrl='" + shortUrl + '\'' +
                ", originIp='" + originIp + '\'' +
                '}';
    }
}
