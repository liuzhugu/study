package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession {

    private static Connection connection;

    private static Map<String,XNode> mappers;

    public DefaultSqlSession(Connection connection,Map<String, XNode> mappers) {
        this.connection = connection;
        this.mappers = mappers;
    }

    @Override
    public <T> T selectOne(String statement,Object parameter) {
        //找到对应mapper
        XNode mapper = mappers.get(statement);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(mapper.getSql());
            //组装参数
            buildParameter(preparedStatement,parameter,mapper.getParameter());
            //执行sql
            ResultSet resultSet = preparedStatement.executeQuery();
            //组装返回值
            List<T> result = resultSet2Obj(resultSet,Class.forName(mapper.getResultType()));
            //返回结果
            return result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildParameter (PreparedStatement preparedStatement,Object parameter,
                                 Map<Integer, String> parameterMap) throws SQLException,IllegalAccessException {
        int size = parameterMap.size();
        //如果参数是普通类型
        if (parameter instanceof Integer) {
            for (int i = 1;i <= parameterMap.size();i ++) {
                preparedStatement.setInt(i,Integer.parseInt(parameter.toString()));
            }
            return;
        }
        if (parameter instanceof Long) {
            for (int i = 1;i <= parameterMap.size();i ++) {
                preparedStatement.setLong(i,Long.parseLong(parameter.toString()));
            }
            return;
        }
        if (parameter instanceof String) {
            for (int i = 1;i <= parameterMap.size();i ++) {
                preparedStatement.setString(i,parameter.toString());
            }
            return;
        }

        //如果参数是类  那么得根据映射  找到每个字段在sql中的位置
        Map<String,Object> fieldMap = new HashMap<>();
        //字段名和字段值的映射
        Field[] fields = parameter.getClass().getDeclaredFields();
        for (Field field : fields) {
            //取出字段名
            String fieldName = field.getName();
            //取出字段值
            field.setAccessible(true);
            Object object = field.get(parameter);
            field.setAccessible(false);
            fieldMap.put(fieldName,object);
        }
        for (int i = 1;i < size;i ++) {
            Object value = fieldMap.get(parameterMap.get(i));
            if (parameter instanceof Short) {
                preparedStatement.setShort(i,Short.parseShort(value.toString()));
            }
            if (parameter instanceof Integer) {
                preparedStatement.setInt(i,Integer.parseInt(value.toString()));
            }
            if (parameter instanceof Long) {
                preparedStatement.setLong(i,Long.parseLong(value.toString()));
            }
            if (parameter instanceof String) {
                preparedStatement.setString(i,value.toString());
            }
            if (parameter instanceof Date) {
                preparedStatement.setDate(i,(Date) value);
            }
        }
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet,Class<?> clazz) {
        //调用setter方法设置进去
        List<T> result = new ArrayList<>();
        try {
            //获取结构信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            //获取字段数目
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T obj = (T)clazz.newInstance();
                //设置值
                for (int i = 1;i <= columnCount;i ++) {
                    //获取返回结果中当前结果行的当前字段的值
                    Object value = resultSet.getObject(i);
                    //还有字段名
                    String columnName = metaData.getColumnName(i);
                    //获取相应的setter方法名 驼峰式 然后将值设置进去
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() +
                            columnName.substring(1);
                    Method method;
                    // Timestamp 对应Date
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, java.util.Date.class);
                    } else {
                        method = clazz.getMethod(setMethod,value.getClass());
                    }
                    method.invoke(obj,value);
                }
                result.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
