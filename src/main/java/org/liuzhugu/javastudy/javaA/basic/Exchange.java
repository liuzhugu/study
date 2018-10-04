package org.liuzhugu.javastudy.javaA.basic;

/**
 * Created by liuting6 on 2017/11/23.
 */
public class Exchange {
    public static void main(String[] args)throws Exception{
        int A=1,B=2;
        //最常用的交换
//        int C=A;
//        A=B;
//        B=C;

        //但若是A+B溢出的话就无法交换
//        A=A+B;
//        B=A-B;
//        A=A-B;

        //按方向规则还原自身
        A=A^B;
        B=A^B;
        A=A^B;
        System.out.println("A is "+A+" and B is "+B);
        Integer.valueOf(1);
    }
}
