package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.util.logging.Level;

/**
 * 抽象类
 *  模板设计模式
 *  抽象类不允许创建实例   必须要在子类中实现抽象方法
 *  这样必须子类重写了方法之后才能使用   这样既复用了公共方法  又达到了扩展性
 * */
public abstract class Logger {
    private String name;
    private boolean enabled;
    private Level minPermittedLevel;

    public Logger(String name, boolean enabled, Level minPermittedLevel) {
        this.name = name;
        this.enabled = enabled;
        this.minPermittedLevel = minPermittedLevel;
    }

    public void log(Level level,String message) {
        //模板模式   父类定义模板  既可以灵活替换部分逻辑   实现复用
        //但父类却又对流程进行了约束   这样复用了父类的方法  也对最终的流程逻辑做了一定的约束
        boolean loggable = enabled && (minPermittedLevel.intValue() <= level.intValue());
        if (! loggable) return;
        doLog(level,message);
    }

    //必须子类实现  那么就开放给子类但不开放给子类之外的类
    protected abstract void doLog(Level level,String message);
}
