package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public interface ImageStore {
    String upload(Image image,String bucketName);
    Image download(String url);
}
