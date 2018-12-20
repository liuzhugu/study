package org.liuzhugu.javastudy.practice.reflection;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewInstanceTest {

    public static void main(String[] args)throws Exception{

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //通过反射创建实例
        //1.使用class对象的newinstance方法来创建class对象相应的实例
        Class clazz= Date.class;
        Date date1=(Date) clazz.newInstance();
        System.out.println(simpleDateFormat.format(date1));

        //2.先通过class对象获取构造器再创建class对象相应的实例
        long time=date1.getTime();
        Date date2=(Date) clazz.getConstructor(long.class).newInstance(time);
        System.out.println(simpleDateFormat.format(date2));
    }
}
