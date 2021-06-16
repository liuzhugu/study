package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.io.FileWriter;
import java.io.Writer;
import java.util.logging.Level;

/**
 * 抽象类的子类：输出日志到文件
 * */
public class FileLogger extends Logger {
    private Writer fileWrite;

    public FileLogger(String name, boolean enabled, Level minPermittedLevel,String filePath) {
        super(name, enabled, minPermittedLevel);
        try {
            this.fileWrite = new FileWriter(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //实现抽象方法  这样就复用了父类的其他方法  又在关键的逻辑上有自己的实现
    @Override
    protected void doLog(Level level, String message) {
        //格式化level和message到文件
        try {
            fileWrite.write("...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
