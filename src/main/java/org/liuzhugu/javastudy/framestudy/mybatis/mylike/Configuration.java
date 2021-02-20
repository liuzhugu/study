package org.liuzhugu.javastudy.framestudy.mybatis.mylike;

import java.sql.Connection;
import java.util.Map;

public class Configuration {
    private Map<String,String> dataSouce;
    private Connection connection;
    private Map<String,XNode> mappers;

    public Map<String, String> getDataSouce() {
        return dataSouce;
    }

    public void setDataSouce(Map<String, String> dataSouce) {
        this.dataSouce = dataSouce;
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
