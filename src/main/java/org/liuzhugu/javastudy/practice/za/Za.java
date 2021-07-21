package org.liuzhugu.javastudy.practice.za;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Za {
    public static void main(String[] args) {
        test(898.1667,24.0);
    }

    private static void test(double arg1,double arg2) {
        String arg11 = arg1+"";
        String arg22 = arg2+"";
        int m = 0;
        try{
            m+=arg11.split("\\.")[1].length();
            System.out.println(m);
        }catch(Exception e){
            System.out.println("计算出错");
        }
        try{
            m+=arg22.split("\\.")[1].length();
            System.out.println(m);
        }catch(Exception e){
            System.out.println("计算出错");
        }
        System.out.println(8981667 * 240);
        double dealerAmount = Integer.parseInt(arg11.replace(".",""))*Integer.parseInt(arg22.replace(".",""))/Math.pow(10,m);
        System.out.println(dealerAmount);
        System.out.println(new BigDecimal(21556.00).setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal(dealerAmount).setScale(2, RoundingMode.HALF_UP))!= 0);
    }
}
