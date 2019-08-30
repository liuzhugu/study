package org.liuzhugu.javastudy.book.javaA.reflection;

/**
 * Created by liuting6 on 2017/12/27.
 */
public class TestMain {
    public static void main(String[] args) {
        System.out.println(XYZ.name);
    }
}

class XYZ {
    public static String name = "luoxn28";

    static {
        System.out.println("xyz静态块");
    }

    public XYZ() {
        System.out.println("xyz构造了");
    }
}