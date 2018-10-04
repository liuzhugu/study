package org.liuzhugu.javastudy.logicjava.chapter1;

import java.io.UnsupportedEncodingException;

/**
 * Created by liuting6 on 2018/1/29.
 * 乱码恢复
 */

public class CharsetStudy {
    public static void main(String[] args)throws Exception{
        //String test=new String("老马".getBytes(),"GB18030");
        //System.out.println(test);
//        String test="鑰侀┈";
//        recover(test);
        char c='挺';
        System.out.println(Integer.toBinaryString(c));
    }
    public  static void recover(String str)throws UnsupportedEncodingException{
        String[] charset=new String[]{"GB18030","UTF-8","Big5","windows-1252"};
        for(int i=0;i<charset.length;i++){
            for(int j=0;j<charset.length;j++){
                if(i!=j){
                    String s=new String(str.getBytes(charset[i]),charset[j]);
                    System.out.println("----原来的编码(A)假设是:"+charset[i]+",被误解读为(B):"+charset[j]);
                    System.out.println(s);
                }
            }
        }
    }
    public static String str1="";
    public String str2="";
    public void test()throws UnsupportedEncodingException{
        String test;
        test=str1;
        test=str2;
        recover(test);
        test1();
    }
    public void test1(){}
}
