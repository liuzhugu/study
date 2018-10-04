package org.liuzhugu.javastudy.practice.log.formatter;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;

/**
 * Created by liuting6 on 2017/11/16.
 */
public class TXTFormatter implements Formatter{
    private String prefix="TXT格式，内容为:";
    @Override
    public LoggerEvent formatter(LoggerEvent e){
        e.setContent(prefix+e.getContent());
        return e;
    }
}
