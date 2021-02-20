package org.liuzhugu.javastudy.framestudy.mybatis.mylike;

public interface SqlSession {
    public <T> T selectOne(String statement,Object parameter);
    public void close();
}
