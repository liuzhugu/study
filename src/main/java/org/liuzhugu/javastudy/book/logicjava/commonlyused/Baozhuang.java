package org.liuzhugu.javastudy.book.logicjava.commonlyused;

/**
 * Created by liuting6 on 2018/2/27.
 */
public class Baozhuang {
    public static void main(String[] args){
        //字符串转换成包装类对象
        Boolean b=Boolean.valueOf("true");
        Float f=Float.valueOf("123.45f");

        //字符串转换成基本类型
        boolean b1=Boolean.parseBoolean("true");
        float f1=Float.parseFloat("123.45f");

        //转换字符串
        //System.out.println(b.toString());
        //System.out.println(f.toString());

        int a=0x12345678;
        System.out.println(Integer.toBinaryString(a));
        int r=Integer.reverse(a);
        System.out.println(Integer.toBinaryString(r));
        int rb=Integer.reverseBytes(a);
        System.out.println(Integer.toHexString(rb));
    }
}
