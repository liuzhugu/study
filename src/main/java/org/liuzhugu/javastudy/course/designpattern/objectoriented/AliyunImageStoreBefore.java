package org.liuzhugu.javastudy.course.designpattern.objectoriented;

/**
 * 不好的设计  当替换实现方案时需要修改的太多
 * */
public class AliyunImageStoreBefore {
    //...省略属性、构造方法等...



    public void createBucketIfNotExisting(String bucketName) {
        //...创建bucket代码逻辑...
        //...失败会抛出异常...
    }

    //该实现方案所特有的方法
    public String generateAccessToken() {
        //...根据accessKey/secrectKey等生成access token...
        return "";
    }

    //接口名暴露了太多实现细节  当更换实现方法时只能修改
    public String uploadToAliyun(Image image,String bucketName,String accessToken) {
        //...上传图片到阿里云...
        //...返回图片存储到阿里云上的地址(url)...
        return "";
    }

    public Image downFromAliyun(String url,String accessToken) {
        //...从阿里云下载图片...
        return new Image();
    }
}
