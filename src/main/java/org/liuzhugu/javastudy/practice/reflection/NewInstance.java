package org.liuzhugu.javastudy.practice.reflection;


import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;

/**
 * 根据反射实例化对象的三种方式
 * */
class User {
    String name = "hello world!";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
public class NewInstance {
    public static void main(String[] args) throws Exception {

        //1.java.lang.Class.newInstance()   会初始化实例变量
        User first = User.class.newInstance();
        System.out.println(first.name);

        //java.lang.Reflect.Constructor.newInstance()   会初始化实例变量
        User second = User.class.getDeclaredConstructor().newInstance();
        System.out.println(second.name);

        //sun.reflect.ReflectionFactory.newConstructorForSerialization()
        // .newInstance()   不会初始化实例变量
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> constructor  = reflectionFactory.newConstructorForSerialization(User.class,Object.class.getDeclaredConstructor());
        constructor.setAccessible(true);
        Object third = constructor.newInstance();
        System.out.println(((User)third).name);
    }
}
