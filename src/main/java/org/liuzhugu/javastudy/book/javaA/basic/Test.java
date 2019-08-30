package org.liuzhugu.javastudy.book.javaA.basic;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuting6 on 2017/11/21.
 */
public class Test {
    public static String getA(){
        return "a";
    }
    public static void main(String[] args){
        //test1
//       String a="a"+"b"+1;
//       String b="ab1";
//       System.out.println(a==b);
//        String a="a";
//        final String c="a";

        //test2
//        String b=a+"b";
//        String d=c+"b";
//        String e=getA()+"b";
//        String compare="ab";
//        //为false，因为字符串内容不可变，但变量指向哪个字符串可以变，所以变量a并不代表常量
//        System.out.println(b==compare);
//        System.out.println(d==compare);
//        System.out.println(e==compare);

        //test3
//        String a="a";
//        String b=a+"b";
//        String c="ab";
//        String d=new String(b);
//        //false,因为a虽然指向字符串，但值仍是不确定的，所以没被编译优化
//        System.out.println(b==c);
//        System.out.println(c==d);
//        //intern的作用是在常量池中查找该字符串的地址，若找不到则在常量池中创建一个新的返回其地址
//        System.out.println(c==d.intern());
//        System.out.println(b.intern()==d.intern());

        //StringBuild和+比较
//        StringBuilder tmp=new StringBuilder();
//        Random random=new Random();
//        long start=System.currentTimeMillis();
//        for(int i=0;i<10000;i++){
//            tmp.append(random.nextInt(10));
//        }
//        System.out.println("复用StringBuild耗费 "+(System.currentTimeMillis()-start)+" ms");
//        String test="";
//        long start1=System.currentTimeMillis();
//        for (int j=0;j<10000;j++){
//            //下面这段代码将会被编译成相当于
//            //StringBuilder tmp=new StringBuilder();
//            //tmp.append(test).append(random.nextInt(10));
//            //test=tmp.toString();
//            //每次都要新建StringBuild，中间还夹杂着多次扩容，因此性能没前者好
//            test=test+random.nextInt(10);
//        }
//        System.out.println("字符串拼接操作耗费 "+(System.currentTimeMillis()-start1)+" ms");
//        long start2=System.currentTimeMillis();
//        String test2="";
//        for(int i=0;i<10000;i++){
//            StringBuilder tmp1=new StringBuilder();
//            tmp1.append(test2);
//            tmp1.append(random.nextInt(10));
//            test2=tmp.toString();
//        }
//        System.out.println("不复用StringBuild耗费 "+(System.currentTimeMillis()-start2)+" ms");


//        int i=1,j=10;
//        do { if(i++>--j) continue; }while (i<5);
//        System.out.println(i+" "+j);
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        System.out.println(getNextDay(dateString,-1));
    }
    private static String getNextDay(String nowdate, int  delay) {
        String mdate = "";
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
        }catch(Exception e){
            System.out.println("日期转换错误");
        }
        return mdate;
    }
    private static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
