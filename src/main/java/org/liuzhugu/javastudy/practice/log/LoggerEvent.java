package org.liuzhugu.javastudy.practice.log;

import java.util.Date;

/**
 * Created by liuting6 on 2017/11/16.
 * 日志本身
 */
public class LoggerEvent {
    private Date timeStamp;
    private String content;

    public LoggerEvent(Date timeStamp,String content){
        this.timeStamp=timeStamp;
        this.content=content;
    }
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
