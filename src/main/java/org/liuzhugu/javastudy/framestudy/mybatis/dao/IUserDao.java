package org.liuzhugu.javastudy.framestudy.mybatis.dao;

import org.liuzhugu.javastudy.framestudy.mybatis.model.User;

public interface IUserDao {

    User queryUserInfoById(int id);

}
