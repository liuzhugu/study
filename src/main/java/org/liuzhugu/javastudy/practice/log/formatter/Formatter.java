package org.liuzhugu.javastudy.practice.log.formatter;

import org.liuzhugu.javastudy.practice.log.LoggerEvent;

/**
 * Created by liuting6 on 2017/11/16.
 */
public interface Formatter {
    public LoggerEvent formatter(LoggerEvent e);
}
