package org.liuzhugu.javastudy.book.effectivejava;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Person {
    private final Date birthday;
    public Person(Date birthday){
        this.birthday=birthday;
    }

    //不会改变的对象，还可以用于类共享
    private static final Date boomStart;
    private static final Date boomEnd;
    static {
        Calendar gmtCal=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
        boomStart=gmtCal.getTime();
        gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
        boomEnd=gmtCal.getTime();
    }


    public boolean isBabyBoomer(){
        Calendar gmtCal=Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        //每次调用都得新建，这两个日期是常量，不会改变，可以在初始化的时候就赋值
//        gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
//        Date boomStart=gmtCal.getTime();
//        gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
//        Date boomEnd=gmtCal.getTime();

        return birthday.compareTo(boomStart)>=0&&birthday.compareTo(boomEnd)<0;
    }

    public static void main(String[] args){
        Person person= new Person(new Date(48,Calendar.JANUARY,1));
        System.out.println(person.isBabyBoomer());
    }
}
