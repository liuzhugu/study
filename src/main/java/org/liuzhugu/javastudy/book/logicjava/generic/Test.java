package org.liuzhugu.javastudy.book.logicjava.generic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Test {
    public static void main(String[] args) throws Exception{

        //根据类名获取class对象
        Class<Date> dateClass = Date.class;

        //接口也有class对象
        Class<Comparable> comparableClass = Comparable.class;

        //根据对象获取class对象
        Date date = new Date();
        Class<? extends Date> dateClass1 = date.getClass();

        //根据类名加载class对象
        try {
            Class<?> cls = Class.forName("java.util.HashMap");
            System.out.println(cls.getName());
        }catch (Exception e){}

        //对于private字段要先关闭检查机制才能运行
        List<String> obj = Arrays.asList(new String[]{"老马","编程"});
        Class<?> cls = obj.getClass();
        //遍历字段
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            System.out.println(field.getName() + " - " + field.get(obj));
        }


        Class<?> integerClass = Integer.class;
        try {
            //根据方法名和参数列表获取方法
            Method method = integerClass.getMethod("parseInt", new Class[]{String.class});
            //调用方法
            System.out.println(method.invoke(null,"123"));
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //通过class的newInstance方法创建对象,其实相当于直接调用无参构造方法
        Map<String,Integer> map = HashMap.class.newInstance();
        map.put("hello",123);

        //检查类型
        List<String> list = new ArrayList<>();

        //通过关键字判断
        if (list instanceof ArrayList) {
            System.out.println("array list");
        }

        //根据传值动态决定
        Class clazz = Class.forName("java.util.ArrayList");
        if (clazz.isInstance(list)) {
            System.out.println("array list");
        }

        //强制类型转换
        //事先知道类型
        List<String> stringList = new ArrayList<>();
        if (stringList instanceof ArrayList) {
            ArrayList arrayList = (ArrayList)stringList;
        }
        //动态
//        public static <T> T toType(Object obj,Class<T> cls){
//            return cls.cast(obj);
//        }

        //获取数组元素类型
        String[] arr = new String[]{};
        System.out.println(arr.getClass().getComponentType());
    }
}
