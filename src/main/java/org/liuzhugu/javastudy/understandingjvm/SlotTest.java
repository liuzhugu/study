package org.liuzhugu.javastudy.understandingjvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuting6 on 2017/10/25.
 * 局部变量表slot复用对垃圾收集的影响之一
 */
public class SlotTest {
    int b;
    public int test(){
        //System.out.println(b);


        int[] a=new int[5];
        //执行碰到异常，且异常没有得到处理，无返回值，直接报错
        //return a[6];
        try {
            return a[6];
        } catch (Exception e) {
            //执行碰到异常，但得到处理，程序继续执行
            return 1;
        }
    }
    public static void main(String[] args){
//        {byte[] placeholder=new byte[64*1024*1024];}
//        //如果没有这句，那么虽然placeholder已经出了作用于，但其所占的slot仍没有被覆盖，那么gc会认为它仍然存活
//        int test=0;
//        System.gc();


        //int a;
        //System.out.println(a);         //不行，a是局部变量，未初始化不能使用
        SlotTest justTest=new SlotTest();
        //justTest.test();                 //可以，b是类变量，即使未赋值，也会被默认赋予零值
        System.out.println(justTest.test());
        System.out.println("continue");
    }
}
