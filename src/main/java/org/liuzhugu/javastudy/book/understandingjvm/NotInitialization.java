package org.liuzhugu.javastudy.book.understandingjvm;

/**
 * Created by liuting6 on 2017/8/4.
 * 被动使用类字段演示一
 * 通过子类引用父类的静态字段，不会触发子类初始化
 */
class SuperClass{
    static {
        System.out.println("SuperClass init!");
    }
    public static int value=123;
}

class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init!!");
    }
    // public static int value=123;  两者都触发
}

class ConstClass{
    static {
        System.out.println("ConstClass init!");
    }
    public static  String HELLOWORLD="hello world!";
}

public class NotInitialization {
    public static void main(String[] args){
        //被动使用类字段演示一
        //通过子类引用父类的静态字段，不会触发子类初始化
        //System.out.println(SubClass.value);
        //被动使用类字段演示二
        //通过数组定义来引用类，不会触发此类的初始化
        //只会有虚拟机自动生成、直接继承与Object的子类，由newarray字节码命令触发
        //SuperClass[] sca=new SuperClass[10];
        //被动使用类字段演示三
        //常量会在编译时存入调用类的常量池中，因为他不会改变，所以尽早确定  即常量传播优化
        //所以无需必须初始化定义常量的类才能调用，所以无需初始化定义常量的类   去掉final会触发
        System.out.println(ConstClass.HELLOWORLD);
    }
}
