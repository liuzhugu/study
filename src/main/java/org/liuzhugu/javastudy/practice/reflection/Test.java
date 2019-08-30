package org.liuzhugu.javastudy.practice.reflection;

import java.lang.reflect.*;

public class Test {

    public static void main(String[] args)throws Exception{

        //class获取方法
        //1.class.forname
        Class c1=Class.forName("org.liuzhugu.javastudy.practice.reflection.Employee");

        //2.通过类
        Class c2=Test.class;

        //3.通过对象的getclass
        Class c3=new Test().getClass();

        //获取实例
        //1.new
        Employee employee1=new Employee("小明","18","读书",9000,"开发",100);
        //2.class的newinstance
        Employee employee2=(Employee) c1.newInstance();
        //3.class获取构造器之后再创建
        Employee employee3=(Employee)c1.getConstructor().newInstance();


        Employee obj=new Employee();
        Class clazz=obj.getClass();
        //判断是否是数组
        if(clazz.isArray()){
            //获取数组元素的类型
            clazz.getComponentType();
            //获取index为0的元素
            Array.get(obj,0);
        }

        //反射调用方法
        Method sayHello=Employee.class.getDeclaredMethod("sayHello");
        sayHello.invoke(employee1);

        //反射获取类的方法字段等
        Class parent=clazz.getSuperclass();
        //自己声明的
        Method[] selfMethods=clazz.getDeclaredMethods();
        //自己拥有的,包含继承而来的
        Method[] allMethods=clazz.getMethods();
        //自己声明的
        Field[] selfFields=clazz.getDeclaredFields();
        //自己拥有的,包含继承而来的
        Field[] allFields=clazz.getFields();
        // 访问私有的属性，需要打开这个设置，否则会报非法访问异常
        AccessibleObject.setAccessible(allFields,true);
    }
}
