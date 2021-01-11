package org.liuzhugu.javastudy.practice.date;

import java.time.LocalDate;

public class DateStudy {
    public static void main(String[] args) {
        //jdk8

        //获取今天日期
        LocalDate localDate1 = LocalDate.now();
        System.out.println(localDate1);
        System.out.println("年:" + localDate1.getYear());
        System.out.println("月:" + localDate1.getMonthValue());
        System.out.println("日:" + localDate1.getDayOfMonth());

        //获取特定日期
        LocalDate localDate2 = LocalDate.of(2020,3,5);
        System.out.println(localDate2);

        //判断日期是否相等
        if (localDate1.equals(localDate2)) {
            System.out.println("相等");
        } else {
            System.out.println("不相等");
        }
    }
}
