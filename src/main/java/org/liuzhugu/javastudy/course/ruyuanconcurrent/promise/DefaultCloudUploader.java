package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

public class DefaultCloudUploader implements CloudUploader {
    private static  String address;
    private static  String username;
    private static  String password;
    private static  String serverDir;


    @Override
    public void init(String address, String username, String password, String serverDir) {
        this.address = address;
        this.username = username;
        this.password = password;
        this.serverDir = serverDir;
    }

    @Override
    public void discount() {

    }
}
