package org.liuzhugu.javastudy.springpractice.ioc.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PrivateRefectTest {

    public static void main(String[] args)throws Exception{
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        Class<?> clazz=classLoader.
                loadClass("org.liuzhugu.javastudy.springpractice.ioc.reflect.PrivateCar");
        PrivateCar car=(PrivateCar) clazz.getConstructor().newInstance();

        //修改字段访问控制
        Field colorField=clazz.getDeclaredField("color");
        colorField.setAccessible(true);
        colorField.set(car,"红色");

        //修改方法控制
        Method driveMethod=clazz.getDeclaredMethod("drive",(Class[])null);
        driveMethod.setAccessible(true);
        driveMethod.invoke(car,(Object[])null);
    }
}
