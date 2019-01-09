package org.liuzhugu.javastudy.framestudy.spring.ioc.reflect;

public class Test {
    public static void main(String[] args)throws Exception{
        //普通调用
        //Car car=new Car();
        //car.setBrand("红旗");
        //Car car=new Car("红旗","黑色",100);

        try {
            //反射调用
            Car car=ReflectTest.initByDefaultConst();
            car.introduce();
        }catch (Throwable e){
            System.out.println(e.getMessage());
        }
    }
}
