package org.liuzhugu.javastudy.practice.log.appender;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;
import org.liuzhugu.javastudy.practice.log.formatter.Formatter;

/**
 * Created by liuting6 on 2017/11/16.
 */
public class ConsoleAppender implements Appender {
    private Formatter formatter;
    private String packageName;
    public ConsoleAppender(String packageName){
        this.packageName=packageName;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void appender(LoggerEvent log){
        //转换格式
        String content=formatter.formatter(log).getContent();
        //写入控制台
        System.out.println(packageName+" 写入信息 "+content+" !");
    }
}
