package org.liuzhugu.javastudy.practice.za;


import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkTest {
    public static void main(String[] args) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String start = "2021/7/15 00:00:00";
        String end = "2021/7/15 23:59:59";
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.setTime(sdf.parse(start));
        endDate.setTime(sdf.parse(end));
        for (int i = 0;i < 60;i ++) {
            startDate.add(Calendar.DATE,-1);
            endDate.add(Calendar.DATE,-1);
            System.out.println(sdf.format(startDate.getTime()) + "," + sdf.format(endDate.getTime()));
        }
    }



}
