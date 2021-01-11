package org.liuzhugu.javastudy.book.logicjava.classloader;

public class ClassLoaderDemo {
    public static void main(String[] args) throws Exception{
        ClassLoader cl = ClassLoaderDemo.class.getClassLoader();
        while (cl != null) {
            System.out.println(cl.getClass().getName());
            cl = cl.getParent();
        }
        System.out.println(String.class.getClassLoader());

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Class<?> cls = classLoader.loadClass("java.util.ArrayList");
            ClassLoader actualLoader = cls.getClassLoader();
            System.out.println(actualLoader);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
