package org.liuzhugu.javastudy.logicjava.chapter21;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 反射的示例
 * */
public class SimpleMapper {
    public static String toString(Object obj) {
        try {
            Class<?> cls = obj.getClass();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(cls.getName() + "\n");
            //遍历字段
            for (Field field : cls.getDeclaredFields()) {
                if (! field.isAccessible()) {
                    field.setAccessible(true);
                }
                stringBuilder.append(field.getName() + "=" + field.get(obj).toString() + "\n");
            }
            return stringBuilder.toString();
        }catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public static Object fromString(String str) {
        try {
            String[] lines = str.split("\n");
            if (lines.length < 1) {
                throw new IllegalArgumentException();
            }
            //获取类名
            Class<?> cls = Class.forName(lines[0]);
            Object obj = cls.newInstance();
            if (lines.length > 1) {
                for (int i = 1;i < lines.length;i ++) {
                    String[] fv = lines[i].split("=");
                    if (fv.length != 2) {
                        throw new IllegalArgumentException(lines[i]);
                    }
                    Field field = cls.getDeclaredField(fv[0]);
                    if (! field.isAccessible()) {
                        field.setAccessible(true);
                    }

                }
            }
            return obj;
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private static void setFieldValue(Field field,Object obj,String value) throws Exception {
        //获取字段类型
        Class<?> type = field.getType();
        //判断赋值
        if (type == int.class) {
            field.setInt(obj,Integer.parseInt(value));
        } else if (type == byte.class) {
            field.setByte(obj,Byte.parseByte(value));
        } else if (type == short.class) {
            field.setShort(obj,Short.parseShort(value));
        } else if (type == long.class) {
            field.setLong(obj,Long.parseLong(value));
        } else if (type == float.class) {
            field.setFloat(obj,Float.parseFloat(value));
        } else if (type == double.class) {
            field.setDouble(obj,Double.parseDouble(value));
        } else if (type == char.class) {
            field.setChar(obj,value.charAt(0));
        } else if (type == boolean.class) {
            field.setBoolean(obj,Boolean.parseBoolean(value));
        } else if (type == String.class) {
            field.set(obj,value);
        }
        //某个类
        else {
            Constructor<?> constructor = type.getConstructor(new Class[]{String.class});
            //递归处理引用
            field.set(obj,constructor);
        }
    }
}
