package org.liuzhugu.javastudy.book.logicjava.classloader;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurableStrategyDemo {
    public static IService createService() {
        try {
            //加载配置,通过配置加载类
            Properties prop = new Properties();
            String fileName = "/Users/liuzhugu/IdeaProjects/study/src/main/java/org/liuzhugu/javastudy/book/logicjava/classloader/config.properties";
            prop.load(new FileInputStream(fileName));
            String className = prop.getProperty("service");
            Class<?> cls = Class.forName(className);
            return (IService) cls.newInstance();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        IService service = createService();
        service.action();
    }
}

