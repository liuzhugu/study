package org.liuzhugu.javastudy.framestudy.mybatis.mylike;

import java.util.Map;

/**
 * 存放每个mapper定义
 * 然后以 命名空间 + 方法名作为key 保存在在map中
 * */
public class XNode {
    //每个mapper需要保存

    //命名空间
    private String nameSpace;
    //id
    private String id;
    //参数类型
    private String parameterType;
    //如果参数有多个字段  每个字段的相对位置
    private Map<Integer, String> parameter;
    //返回类型
    private String resultType;
    //要预处理的sql
    private String sql;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<Integer, String> parameter) {
        this.parameter = parameter;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
