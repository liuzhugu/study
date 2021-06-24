package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

public class CloudSyncConfig {
    private  String cloudAddress;
    private  String username;
    private  String password;
    private  String serverDir;

    public CloudSyncConfig(String cloudAddress,String username,String password,String serverDir) {
        this.cloudAddress = cloudAddress;
        this.username = username;
        this.password = password;
        this.serverDir = serverDir;
    }

    public String getCloudAddress() {
        return cloudAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerDir() {
        return serverDir;
    }
}
