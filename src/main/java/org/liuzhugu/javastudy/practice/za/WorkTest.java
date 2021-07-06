package org.liuzhugu.javastudy.practice.za;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkTest {
    public static void main(String[] args) {
        String str = "123.0";
        try {
            int result = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
