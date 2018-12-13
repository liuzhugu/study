package org.liuzhugu.javastudy.practice.za;



public class ClassLoaderStudy {

    public static void main(String[] args)throws Exception{
        //两个方法都可以用来加载目标类，它们之间有一个小小的区别，那就是Class.forName()方法可以获取原生类型的Class，
        // 而ClassLoader.loadClass()则会报错

//        Class<?> x = Class.forName("[I");
//        System.out.println(x);
//
//        Class<?> y=ClassLoader.getSystemClassLoader().loadClass("[I");
//        System.out.println(y);



    }

}
