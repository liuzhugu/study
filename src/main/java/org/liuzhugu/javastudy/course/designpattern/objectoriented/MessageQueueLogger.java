package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.util.logging.Level;

/**
 * 抽象类的子类: 输出日志到消息中间件(比如kafka)
 * */
public class MessageQueueLogger extends Logger {
    private MessageQueueClient msgQueueLogger;

    public MessageQueueLogger(String name, boolean enabled, Level minPermittedLevel,
                              MessageQueueClient messageQueueClient) {
        super(name, enabled, minPermittedLevel);
        this.msgQueueLogger = messageQueueClient;
    }

    @Override
    protected void doLog(Level level, String message) {
        //将level和message输出到信息中间件
        msgQueueLogger.send();
    }
}
