package org.liuzhugu.javastudy.course.designpattern.objectoriented;


public class ImageProcessingJob {
    private static final String BUCKET_NAME = "ai_images_bucket";
    //...省略无关代码...

    public void process() {
        //实现方法与实现耦合太严重  换种实现方案  这里的代码都要改
        Image image = new Image();
        AliyunImageStoreBefore before = new AliyunImageStoreBefore();
        before.createBucketIfNotExisting(BUCKET_NAME);
        String accessToken = before.generateAccessToken();
        before.uploadToAliyun(image,BUCKET_NAME,accessToken);
    }
}
