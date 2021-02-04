package org.liuzhugu.javastudy.framestudy.mybatis.like;

import lombok.Data;
import java.sql.Connection;
import java.util.Map;

@Data
public class Configuration {

    private Map<String,String> dataSource;

    private Connection connection;

    private Map<String, XNode> mapperElement;
}
