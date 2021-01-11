package org.liuzhugu.javastudy.book.logicjava.classloader;

public class CLInitDemo {
    public static class Hello {
        static {
            System.out.println("Hello");
        }
    }

    public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        String className = CLInitDemo.class.getName() + "$Hello";
        try {
            //不执行类的初始化代码
            //Class<?> cls = cl.loadClass(className);
            //执行类的初始化代码
            Class<?> cls = Class.forName(className);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
