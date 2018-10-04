package org.liuzhugu.javastudy.practice.log.formatter;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;

/**
 * Created by liuting6 on 2017/11/16.
 * 把信息转换成XML模式，这里即内容里加入xml前缀
 */
public class XMLFormatter implements Formatter{
    private String prefix="XML:格式，内容为";
    @Override
    public LoggerEvent formatter(LoggerEvent e){
         e.setContent(prefix+e.getContent());
         return e;
    }
}
