package org.liuzhugu.javastudy.practice.log.appender;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;
import org.liuzhugu.javastudy.practice.log.formatter.Formatter;

/**
 * Created by liuting6 on 2017/11/16.
 */
public interface Appender {
    public void setFormatter(Formatter formatter);
    public void appender(LoggerEvent log);
}
