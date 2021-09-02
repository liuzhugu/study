package org.liuzhugu.javastudy.framestudy.spring.ioc;

public class TestUtil {

    //等着依赖注入
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
