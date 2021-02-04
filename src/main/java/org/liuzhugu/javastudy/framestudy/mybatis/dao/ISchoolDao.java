package org.liuzhugu.javastudy.framestudy.mybatis.dao;

import org.liuzhugu.javastudy.framestudy.mybatis.model.School;

public interface ISchoolDao {
    School querySchoolInfoById(int id);
}
