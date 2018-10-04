package org.liuzhugu.javastudy.practice.log;

import org.liuzhugu.javastudy.practice.log.appender.Appender;
import org.liuzhugu.javastudy.practice.log.appender.ConsoleAppender;
import org.liuzhugu.javastudy.practice.log.appender.FileAppender;
import org.liuzhugu.javastudy.practice.log.formatter.Formatter;
import org.liuzhugu.javastudy.practice.log.formatter.HTMLFormatter;
import org.liuzhugu.javastudy.practice.log.formatter.TXTFormatter;
import org.liuzhugu.javastudy.practice.log.formatter.XMLFormatter;

/**
 * Created by liuting6 on 2017/11/16.
 * 日志工具
 */
public class Logger {
    private String packageName;
    public Logger(String packageName){
        this.packageName=packageName;
    }

    //根据类名或包名来决定日志的输出目的地的不同，应该是写在xml文件中然后加载的，
    //这里简化为硬编码,且appender从list简化为单个
    public Appender getAppender(){
        Appender appender=null;
        switch (packageName){
            case "com.foo.file.txt":
                Formatter txtFormatter=new TXTFormatter();
                appender=new FileAppender("D:\\coding\\javastudy\\foo.txt");
                appender.setFormatter(txtFormatter);
                break;
            case "com.foo.console.xml":
                Formatter xmlFormatter=new XMLFormatter();
                appender=new ConsoleAppender("com.foo.console.xml");
                appender.setFormatter(xmlFormatter);
                break;
            case "com.bar.file.txt":
                Formatter txtFormatter1=new TXTFormatter();
                appender=new FileAppender("D:\\coding\\javastudy\\bar.txt");
                appender.setFormatter(txtFormatter1);
                break;
            case "com.bar.console.xml":
                Formatter xmlFormatter1=new XMLFormatter();
                appender=new ConsoleAppender("com.bar.console.xml");
                appender.setFormatter(xmlFormatter1);
                break;
            case "com.test.console.html":
                Formatter htmlFormatter=new HTMLFormatter();
                appender=new ConsoleAppender("com.test.console.html");
                appender.setFormatter(htmlFormatter);
                break;
            default:
                Formatter txtFormatter2=new TXTFormatter();
                appender=new ConsoleAppender("com.default");
                appender.setFormatter(txtFormatter2);
        }
        return appender;
    }


    public static Logger getLogger(String packageName){
        return new Logger(packageName);
    }

    public void log(LoggerEvent log){
        getAppender().appender(log);
    }
}
