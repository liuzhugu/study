package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class PrivateImageStore implements ImageStore {
    //...省略属性、构造方法等...

    public String upload(Image image,String bucketName) {
        createBucketIfNotExisting(bucketName);
        //...上传图片到私有云...
        //...返回图片url...
        return "";
    }

    public Image download(String url) {
        //...从私有云下载图片...
        return new Image();
    }



    private void createBucketIfNotExisting(String bucketName) {
        //...创建bucket代码逻辑...
        //...失败会抛出异常...
    }

}
