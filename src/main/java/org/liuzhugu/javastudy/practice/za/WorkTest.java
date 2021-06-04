package org.liuzhugu.javastudy.practice.za;


import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkTest {
    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm");
        System.out.println(simpleDateFormat.format(new Date()));
    }
}
