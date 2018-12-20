package org.liuzhugu.javastudy.practice.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ObjectAnalyzer {
    private List<Object> visited=new ArrayList<>();

    public String toString(Object obj){

        if(obj==null){
            return "null";
        }

        if(visited.contains(obj)){
            return "...";
        }
        visited.add(obj);

        Class clazz=obj.getClass();
        if(clazz==String.class){
            return (String)obj;
        }
        if(clazz.isArray()){
            //数组的元素的类型
            String r=clazz.getComponentType()+"[]{\n";
            for(int i=0;i<Array.getLength(obj);i++){
                if (i > 0) {   // 不是数组的第一个元素加逗号和换行，显示更加美观
                    r += ",\n";
                }
                r += "\t";
                Object val=Array.get(obj,i);
                // Class为8种基本类型的时候为 true，直接输出
                if(clazz.getComponentType().isPrimitive()){
                    r += val;
                }
                // class为class
                else {
                    r += toString(val);
                }
            }
            return r + "\n}";
        }
        // 既不是String，也不是数组时，输出该对象的类型和属性值
        String r=clazz.getName();
        do{
            r += "[";
            // 获取该类自己定义的所有域，包括私有的，不包括父类的
            Field[] fields=clazz.getDeclaredFields();
            // 访问私有的属性，需要打开这个设置，否则会报非法访问异常
            AccessibleObject.setAccessible(fields,true);
            for(Field field:fields){
                // 通过 Modifier 可获取该域的修饰符，这里判断是否为 static
                if(!Modifier.isStatic(field.getModifiers())){
                    if(!r.endsWith("[")){
                        r+=",";
                    }
                    // 域名称
                    r+=field.getName()+"=";
                    try {
                        // 域（属性）的类型
                        Class t=field.getType();
                        Object val=field.get(obj);
                        if (t.isPrimitive()) {     // 如果类型为8种基本类型，则直接输出
                            r += val;
                        } else {
                            r += toString(val);     // 不是8种基本类型，递归调用toString
                        }
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
            }
            r += "]";
            clazz = clazz.getSuperclass(); // 继续打印超类的类信息
        }while (clazz!=null);
        return r;
    }
}
