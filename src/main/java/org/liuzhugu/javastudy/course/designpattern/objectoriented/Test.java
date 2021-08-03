package org.liuzhugu.javastudy.course.designpattern.objectoriented;


import java.util.logging.Level;

public class Test {
    private static final String BUCKET_NAME = "ai_images_bucket";

    public static void main(String[] args) {
        //05  封装
        Wallet wallet = new Wallet();
        //05  多态
        DynamicArray sortedDynamicArray = new SortedDynamicArray();
        //06  面向对象与面向过程
        UserFileFormatter userFileFormatter = new UserFileFormatter();
        //07  避免面向对象写成面向过程
        ShoppingCart shoppingCart = new ShoppingCart();
        //08  抽象类
        Logger fileLogger = new FileLogger("",true,Level.INFO,"");
        Logger msgQueueLogger = new MessageQueueLogger("",true,Level.WARNING,new MessageQueueClient());
        //08  接口
        Filter authencationFilter = new AuthencationFilter();
        Filter rateLimitFilter = new RateLimitFilter();
        //09  抽象的不够的实现  当更换实现方案时  需要改动太大
        Image image = new Image();
        AliyunImageStoreBefore imageStore = new AliyunImageStoreBefore();
        imageStore.createBucketIfNotExisting(BUCKET_NAME);
        String accessToken = imageStore.generateAccessToken();
        imageStore.uploadToAliyun(image,BUCKET_NAME,accessToken);
        //09  抽象以后  可以自由替换
        ImageStore newImageStore = new AliyunImageStore();
        newImageStore.upload(image,BUCKET_NAME);
        //轻松替换  而不用修改API   如果再配合spring的IOC  那么代码无需任何变动  只需修改配置
        newImageStore = new PrivateImageStore();
        newImageStore.upload(image,BUCKET_NAME);
        //10 多用组合少用继承
        Ostrich ostrich = new Ostrich();
    }
}
