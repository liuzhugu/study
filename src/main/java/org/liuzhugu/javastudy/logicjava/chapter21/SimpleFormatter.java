package org.liuzhugu.javastudy.logicjava.chapter21;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 定制序列化
 * */
public class SimpleFormatter {
    public static String format(Object object) {
        try {
            Class<?> cls = object.getClass();
            StringBuilder stringBuilder = new StringBuilder();
            //获取所有方法,判断是否有相应的注解
            for (Field field : cls.getDeclaredFields()) {
                //修改访问权限
                if (! field.isAccessible()) {
                    field.setAccessible(true);
                }
                //获取注解
                Label label = field.getAnnotation(Label.class);
                String name = (label != null ? label.value() : field.getName());
                Object value = field.get(object);
                if (value != null && field.getType() == Date.class) {
                    value = fommatDate(field,value);
                }
                stringBuilder.append(name + ": " + value + "\n");
            }
            return stringBuilder.toString();
        }catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
    private static Object fommatDate(Field field,Object value) {
        Format format = field.getAnnotation(Format.class);
        if (format != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format.pattern());
            sdf.setTimeZone(TimeZone.getTimeZone(format.timezone()));
            return sdf.format(value);
        }
        return value;
    }
}
