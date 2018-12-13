package org.liuzhugu.javastudy.book.javaA.concurrency;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by liuting6 on 2018/1/23.
 */
public class SemaphoreTest {
    private static final Semaphore MAX_SEMA_PHORE=new Semaphore(5);
    private final static SimpleDateFormat DEFAULT_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getDateTime(){
        Date dateTime= Calendar.getInstance().getTime();
        return DEFAULT_DATE_FORMAT.format(dateTime);
    }
    public static void main(String[] args){
        for(int i=0;i<20;i++){
            final int num=i;
            final Random random=new Random();
            new Thread(){
                public void run(){
                    boolean acquired=false;
                    try{
                        MAX_SEMA_PHORE.acquire();
                        acquired=true;
                        System.out.println("我是线程:"+num+"我获得了使用权! "+getDateTime());
                        Thread.sleep(Math.abs(random.nextInt())%1000);
                        System.out.println("我是线程:"+num+"我执行完毕了! "+getDateTime());
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        if(acquired==true){
                            MAX_SEMA_PHORE.release();
                        }
                    }
                }
            }.start();
        }
    }
}
