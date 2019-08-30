package org.liuzhugu.javastudy.framestudy.spring.ioc;

import org.liuzhugu.javastudy.framestudy.spring.ioc.reflect.Car;

import java.lang.reflect.Field;

public class ClassLoaderTest {

    public static void main(String[] args)throws Exception{

        //使用appLoader显式加载
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        Class<?> clazz = classLoader.loadClass("org.liuzhugu.javastudy.framestudy.spring.ioc.reflect.Car");
        Car customizeCar = (Car) clazz.getConstructor().newInstance();

        //默认加载
        Car defaultCar=new Car();

        System.out.println(defaultCar.getClass().getClassLoader());

        System.out.println("current classloader:"+classLoader);
        System.out.println("parent classloader:"+classLoader.getParent());
        System.out.println("granparent classloader:"+classLoader.getParent().getParent());

        //获取所有方法
//        Method[] methods=clazz.getMethods();
//        for(Method method:methods){
//            System.out.println(method.getName());
//        }

        //获取所有属性
        System.out.println("Fields have:");
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            System.out.println(field.getName());
        }

    }
}
