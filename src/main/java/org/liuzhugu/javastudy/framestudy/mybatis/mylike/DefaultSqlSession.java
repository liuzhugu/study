package org.liuzhugu.javastudy.framestudy.mybatis.mylike;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession{

    private Connection connection;
    private Map<String,XNode> mappers;

    public DefaultSqlSession(Connection connection,Map<String,XNode> mappers) {
        this.connection = connection;
        this.mappers = mappers;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        //获取对应的mapper
        XNode mapper = mappers.get(statement);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(mapper.getSql());
            //组装参数
            buildParameter(preparedStatement,parameter,mapper.getParameter());
            //执行获得返回值
            ResultSet resultSet = preparedStatement.executeQuery();
            //组装返回值
            List<T> result = resultSet2Obj(resultSet,Class.forName(mapper.getResultType()));
            //返回结果
            return result.get(0);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildParameter(PreparedStatement preparedStatement,Object parameter,
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

        //如果参数是对象
        Map<String,Object> fieldMap = new HashMap<>();
        Field[] fields = parameter.getClass().getDeclaredFields();
        for (Field field : fields) {
            //取出字段名
            String fieldName = field.getName();
            field.setAccessible(true);
            //获取字段值
            Object obj = field.get(parameter);
            field.setAccessible(false);
            //将每个字段的名字和值设置进来
            fieldMap.put(fieldName,obj);
        }
        //判断每个位置要插入的参数是什么字段名  然后获取其值  然后判断类型插入进去
        for (int i = 1;i <= parameterMap.size();i ++) {
            String parameterDefine = parameterMap.get(i);
            Object obj = fieldMap.get(parameterDefine);
            if (obj instanceof Short) {
                preparedStatement.setShort(i,Short.parseShort(parameter.toString()));
            }
            if (obj instanceof Integer) {
                preparedStatement.setInt(i,Integer.parseInt(parameter.toString()));
            }
            if (obj instanceof Long) {
                preparedStatement.setLong(i,Long.parseLong(parameter.toString()));
            }
            if (obj instanceof String) {
                preparedStatement.setString(i,parameter.toString());
            }
            if (obj instanceof Date) {
                preparedStatement.setDate(i,(Date)obj);
            }
        }
    }


    private <T> List<T> resultSet2Obj(ResultSet resultSet,Class<?> clazz) {
        List<T> result = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colunmCount = metaData.getColumnCount();
            //获取每一个返回结果
            while (resultSet.next()) {
                //获取对应的对象
                T obj = (T)clazz.newInstance();
                for (int i = 1;i <= colunmCount;i ++) {
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

    //关闭连接
    @Override
    public void close() {
        if (connection == null)return;
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
