package org.liuzhugu.javastudy.practice.log;

import java.util.Date;
import java.util.Random;

/**
 * Created by liuting6 on 2017/11/16.
 * 测试日志系统
 */
public class Test {
    private final static int THREADS_COUNT=5;
    static int count=0;
    public static int getCount(){
        synchronized (Test.class){
            count++;
            return count;
        }
    }
    public static void main(String[] args){
        final Logger[] loggerList=new Logger[5];
        loggerList[0]=Logger.getLogger("com.foo.file.txt");
        loggerList[1]=Logger.getLogger("com.foo.console.xml");
        loggerList[2]=Logger.getLogger("com.bar.file.txt");
        loggerList[3]=Logger.getLogger("com.bar.console.xml");
        loggerList[4]=Logger.getLogger("com.test.console.html");
        final Thread[] threads=new Thread[THREADS_COUNT];
        for(int i=0;i<THREADS_COUNT;i++){
            threads[i]=new Thread(new Runnable() {
                public void run() {
                    for(int i=0;i<100;i++){
                       try {
                           Random random=new Random();
                           int count=random.nextInt(5);
                           Thread tmp=Thread.currentThread();
                           tmp.sleep(random.nextInt(100));
                           loggerList[count].log(new LoggerEvent(new Date(System.currentTimeMillis()),"我是由"+tmp.getName()+"线程写入的第"+Test.getCount()+"条测试信息"));
                       }catch (InterruptedException e){

                       }
                    }
                }
            }
            );
            threads[i].start();
        }
    }
}
