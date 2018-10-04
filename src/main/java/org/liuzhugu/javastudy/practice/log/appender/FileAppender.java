package org.liuzhugu.javastudy.practice.log.appender;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;
import org.liuzhugu.javastudy.practice.log.formatter.Formatter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by liuting6 on 2017/11/16.
 * 往文件中写入日志
 */
public class FileAppender implements Appender{
    private String filePath;
    private Formatter formatter;
    public FileAppender(String filePath){
        this.filePath=filePath;
    }

    @Override
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void appender(LoggerEvent log){
        //转换格式
        String content=formatter.formatter(log).getContent();
        Date date=log.getTimeStamp();
        String dateTime=(date.getYear()+1900)+"/"+date.getMonth()+"/"+date.getDate()+" "+date.getHours()+":"+date.getMinutes();
        //追加方式写入文件
        try {
            FileWriter fw=new FileWriter(filePath,true);
            fw.write("于 "+dateTime+" 写入 "+content+" \r\n");
            fw.flush();
            fw.close();
        }catch (IOException e){
            System.out.println("往 "+filePath+" 写入 "+content+" 失败!");
        }
    }
}
