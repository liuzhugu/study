package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadlocal;

public class Test {
    public static void main(String[] args) throws Exception{
        UserPasswordSystemManager userPasswordSystemManager = UserPasswordSystemManager.getInstance();
        userPasswordSystemManager.register("xiaozhang","18928785679");
        userPasswordSystemManager.register("xiaoli","18200177726");
        userPasswordSystemManager.register("xiaozhang","987685625");
        Thread.sleep(1000);
    }
}
