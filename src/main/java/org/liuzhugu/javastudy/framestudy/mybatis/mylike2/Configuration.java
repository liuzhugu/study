package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;

import java.sql.Connection;
import java.util.Map;

public class Configuration {
    //数据源
    private Map<String, String> dataSource;
    //连接
    private Connection connection;
    //mapper映射
    private Map<String,XNode> mappers;

    public Map<String, String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Map<String, XNode> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, XNode> mappers) {
        this.mappers = mappers;
    }
}
