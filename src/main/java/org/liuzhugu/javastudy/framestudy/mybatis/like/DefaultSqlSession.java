package org.liuzhugu.javastudy.framestudy.mybatis.like;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DefaultSqlSession implements SqlSession{
    private Connection connection;

    private Map<String, XNode> mapperElement;

    public DefaultSqlSession(Connection connection, Map<String, XNode> mapperElement) {
        this.connection = connection;
        this.mapperElement = mapperElement;
    }

    @Override
    public <T> T selectOne(String statement) {
        try {
            //方法名到mapper的映射
            XNode xNode = mapperElement.get(statement);
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
            return objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        //方法名到mapper的映射
        XNode xNode = mapperElement.get(statement);
        //获取参数映射
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            //为sql生成预处理器
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            //为预处理器设置参数  替换掉那个占位符
            buildParameter(preparedStatement,parameter,parameterMap);
            //执行sql
            ResultSet resultSet = preparedStatement.executeQuery();
            //组装参数
            List<T> objects = resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
            return objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement) {
        XNode xNode = mapperElement.get(statement);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            buildParameter(preparedStatement,parameter,parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //参数组装
    protected void buildParameter(PreparedStatement preparedStatement,Object parameter,
                                  Map<Integer, String> parameterMap) throws SQLException,IllegalAccessException {
        int size = parameterMap.size();

        //单个参数
        if (parameter instanceof Integer) {
            for(int i = 1;i <= size;i ++) {
                preparedStatement.setInt(i,Integer.parseInt(parameter.toString()));
                return;
            }
        }
        if (parameter instanceof Long) {
            for(int i = 1;i <= size;i ++) {
                preparedStatement.setLong(i,Long.parseLong(parameter.toString()));
                return;
            }
        }
        if (parameter instanceof String) {
            for(int i = 1;i <= size;i ++) {
                preparedStatement.setString(i,parameter.toString());
                return;
            }
        }

        //如果不是单个参数 那么就是对象

        Map<String,Object> fieldMap = new HashMap<>();
        //将参数的所有字段取出来遍历处理
        Field[] declareFields = parameter.getClass().getDeclaredFields();
        for(Field field : declareFields) {
            String name = field.getName();
            field.setAccessible(true);
            Object obj = field.get(parameter);
            field.setAccessible(false);
            fieldMap.put(name,obj);
        }
        //比对XML中设置的参数类型   设置参数
        for (int i = 1;i <= size;i ++) {
            String parameterDefine = parameterMap.get(i);
            Object obj = fieldMap.get(parameterDefine);

            if (obj instanceof Short) {
                preparedStatement.setShort(i, Short.parseShort(obj.toString()));
                continue;
            }
            if (obj instanceof Integer) {
                preparedStatement.setInt(i, Integer.parseInt(obj.toString()));
                continue;
            }
            if (obj instanceof Long) {
                preparedStatement.setLong(i, Long.parseLong(obj.toString()));
                continue;
            }
            if (obj instanceof String) {
                preparedStatement.setString(i, obj.toString());
                continue;
            }
            if (obj instanceof Date) {
                preparedStatement.setDate(i, (java.sql.Date) obj);
            }
        }
    }

    //组装返回结果
    private <T> List<T> resultSet2Obj(ResultSet resultSet,Class<?> clazz) {
        List<T> list = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                //调用每个字段的setter方法  将返回值setter进去
                T obj = (T) clazz.newInstance();
                for (int i = 1;i <= columnCount;i ++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    //setter + 驼峰式
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() +
                            columnName.substring(1);
                    Method method;
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod,Date.class);
                    } else {
                        method = clazz.getMethod(setMethod,value.getClass());
                    }
                    method.invoke(obj,value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //返回结果组装
    @Override
    public void close() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
