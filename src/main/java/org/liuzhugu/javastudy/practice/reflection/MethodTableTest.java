package org.liuzhugu.javastudy.practice.reflection;

import java.lang.reflect.Method;

/**
 * 调用任意方法程序
 * */
public class MethodTableTest {
    public static void main(String[] args)throws Exception{
        Employee employee = new Employee("小明", "18", "写代码", 100000, "Java攻城狮", 1);

        Method sayHello =employee.getClass().getDeclaredMethod("sayHello");
        System.out.println(sayHello);
        sayHello.invoke(employee);

        double x=3.0;
        Method square = MethodTableTest.class.getDeclaredMethod("square", double.class);
        double y1=(double)square.invoke(null,x);
        System.out.printf("square    %-10.4f -> %10.4f%n", x, y1);

        Method sqrt=Math.class.getDeclaredMethod("sqrt", double.class);
        double y2=(double)sqrt.invoke(null,x);
        System.out.printf("sqrt      %-10.4f -> %10.4f%n", x, y2);
    }

    // static静态方法 计算乘方
    public static double square(double x){
        return x*x;
    }
}
