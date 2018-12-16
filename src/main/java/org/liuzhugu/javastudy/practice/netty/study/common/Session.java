package org.liuzhugu.javastudy.practice.netty.study.common;

import lombok.Data;

@Data
public class Session {
    //用户唯一性标示
    private int userId;
    private String userName;

    public Session(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
