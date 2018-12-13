package org.liuzhugu.javastudy.practice.za;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

public class ClassLoaderStudy {

    public static void main(String[] args)throws Exception{
        //两个方法都可以用来加载目标类，它们之间有一个小小的区别，那就是Class.forName()方法可以获取原生类型的Class，
        // 而ClassLoader.loadClass()则会报错

//        Class<?> x = Class.forName("[I");
//        System.out.println(x);
//
//        Class<?> y=ClassLoader.getSystemClassLoader().loadClass("[I");
//        System.out.println(y);

        System.out.println(getVirtualPassportNo());


    }
    private static String getVirtualPassportNo(){
        //E开头,然后是随机八位数字
        StringBuilder virtualPassportNo=new StringBuilder("E");
        Random random=new Random();
        for(int i=0;i<8;i++){
            virtualPassportNo.append(random.nextInt(10));
        }
        return virtualPassportNo.toString();
    }


}
