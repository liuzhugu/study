package org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable;

/**
 * 多线程情况下  需要变更该类的多个字段  那么将该类变为不可变类
 * 让该类的所有字段的变更操作视为一个整体  变为原子性的  那么就不会产生并发问题了
 * */
public final class SmsInfo {

    /**
     * 短信服务商的id
     * */
    private final Long id;


    /**
     * 短信服务商的请求url
     * */
    private final String url;

    /**
     * 短信内容最多多少字节
     * */
    private final Long maxSizeInBytes;

    public SmsInfo(Long id,String url,Long maxSizeInBytes) {
        this.id = id;
        this.url = url;
        this.maxSizeInBytes = maxSizeInBytes;
    }

    /**
     * 初始化
     *
     * @param smsInfo 短信中心
     * */
    public SmsInfo(SmsInfo smsInfo) {
        this.id = smsInfo.id;
        this.url = smsInfo.url;
        this.maxSizeInBytes = smsInfo.maxSizeInBytes;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Long getMaxSizeInBytes() {
        return maxSizeInBytes;
    }

//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setMaxSizeInBytes(Long maxSizeInBytes) {
//        this.maxSizeInBytes = maxSizeInBytes;
//    }
}
