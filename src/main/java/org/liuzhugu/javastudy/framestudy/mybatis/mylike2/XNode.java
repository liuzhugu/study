package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;

import java.util.Map;

//命名空间 + id 唯一确定一个XNode
public class XNode {
    //命名空间
    private String nameSpace;
    //id
    private String id;
    //参数类型
    private String parameterType;
    //参数映射关系
    private Map<Integer, String> parameter;
    //返回值类型
    private String resultType;
    //sql
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
