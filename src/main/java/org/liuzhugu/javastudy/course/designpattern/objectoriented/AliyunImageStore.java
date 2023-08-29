package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class AliyunImageStore implements ImageStore {

    //...省略属性、构造方法等...

    public String upload(Image image,String bucketName) {
        //将实现方案特有细节封装在通用方法内  这样可以自由替换实现方案
        createBucketIfNotExisting(bucketName);
        String token = generateAccessToken();
        //...上传图片到阿里云...
        //...返回图片存储到阿里云上的地址(url)...
        return "";
    }

    public Image download(String url) {
        String token = generateAccessToken();
        //...从阿里云下载图片...
        return new Image();
    }


    //该实现方案所特有的方法
    private void createBucketIfNotExisting(String bucketName) {
        //...创建bucket代码逻辑...
        //...失败会抛出异常...
    }

    private String generateAccessToken() {
        //...根据accessKey/secrectKey等生成access token...
        return "";
    }
}
