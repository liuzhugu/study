package org.liuzhugu.javastudy.practice.log.formatter;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;

/**
 * Created by liuting6 on 2017/11/16.
 * 把信息转换成HTML模式，这里只是在内容里加入html前缀
 */
public class HTMLFormatter implements Formatter{
    private String prefix="HTML格式，内容为:";


    @Override
    public LoggerEvent formatter(LoggerEvent e){
        e.setContent(prefix+e.getContent());
        return e;
    }
}
