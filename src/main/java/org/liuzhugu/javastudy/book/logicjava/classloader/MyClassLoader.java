package org.liuzhugu.javastudy.book.logicjava.classloader;

import org.liuzhugu.javastudy.book.logicjava.file.BinaryFileUtils;

import java.io.IOException;

public class MyClassLoader extends ClassLoader {

    //设置class文件路径
    private static final String BASE_DIR = "/Users/liuzhugu/IdeaProjects/study/target/classes/";

    @Override
    //重新定义查找class方法
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String fileName = name.replaceAll("\\.","/");
        fileName = BASE_DIR + fileName + ".class";
        try {
            //加载文件成字节流
            byte[] bytes = BinaryFileUtils.readFileToByteArray(fileName);
            return defineClass(name,bytes,0,bytes.length);
        }catch (IOException e) {
            throw new ClassNotFoundException("failded to load class " + name,e);
        }
    }
    public static void main(String[] args) throws Exception{
        MyClassLoader classLoader = new MyClassLoader();
        String className = "org.liuzhugu.javastudy.book.logicjava.classloader.ServiceB";
        Class<?> cls = classLoader.findClass(className);
        System.out.println(cls.getName());

        //不同类加载器加载同一个类被JVM视为不同class对象
        MyClassLoader cl1 = new MyClassLoader();
        Class<?> class1 = cl1.loadClass(className);
        ClassLoader cl2 = ClassLoader.getSystemClassLoader();
        Class<?> class2 = cl2.loadClass(className);
        if (class1 != class2) {
            System.out.println("different classes");
        }
    }
}
