package org.liuzhugu.javastudy.springpractice.ioc.reflect;

import java.lang.reflect.Method;

public class ReflectTest {

    public static Car initByDefaultConst()throws Throwable{

        //1.通过类加载器获取Car类对象
        Class<?> clazz= Thread.currentThread().getContextClassLoader().loadClass("org.liuzhugu.javastudy.springpractice.ioc.reflect.Car");

        //2.获取类的默认构造器对象并通过它实例化Car
        Car car=(Car)clazz.getConstructor().newInstance();

        //通过反射方法设置属性
        Method setBrand=clazz.getDeclaredMethod("setBrand",String.class);
        setBrand.invoke(car,"红旗");
        Method setColor=clazz.getDeclaredMethod("setColor",String.class);
        setColor.invoke(car,"黑色");
        Method setMaxSpeed=clazz.getDeclaredMethod("setMaxSpeed",int.class);
        setMaxSpeed.invoke(car,100);

        return car;
    }

}
