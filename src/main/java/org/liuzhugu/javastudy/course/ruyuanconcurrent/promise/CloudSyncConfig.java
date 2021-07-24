package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;


/**
 * Description:云盘同步配置类
 **/
public class CloudSyncConfig {
    /**
     * server端的地址
     */
    private  String cloudAddress;
    /**
     * 账号
     */
    private  String username;
    /**
     * 密码
     */
    private  String password;
    /**
     * 上次到个人网盘的目录
     */
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
