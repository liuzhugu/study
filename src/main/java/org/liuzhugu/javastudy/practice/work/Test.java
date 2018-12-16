package org.liuzhugu.javastudy.practice.work;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws Exception{
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = simpleDateFormat.format(new Date()).toString();
        System.out.println(timeStamp);
    }



}
