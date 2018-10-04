package org.liuzhugu.javastudy.understandingjvm;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liuting6 on 2017/10/24.
 * 不同的加载器对instanceOf关键字运算的结果的影响
 */
public class ClassLoaderTest {
    public static  void main(String[] args)throws Exception{
        ClassLoader myload=new ClassLoader() {
            @Override
            public Class<?> loadClass(String name)throws ClassNotFoundException{
                try{
                    //得到要加载的类的类名
                    String fileName=name.substring(name.lastIndexOf(".")+1)+".class";
                    //加载字节流
                    InputStream is=getClass().getResourceAsStream(fileName);
                    if(is==null){
                        //委托双亲加载
                        return super.loadClass(name);
                    }
                    //读取
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    //转换
                    return defineClass(name,b,0,b.length);
                }catch (IOException e){
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object object=myload.loadClass("org.liuzhugu.javastudy.understandingjvm.ClassLoaderTest")
                            //加载成功后生成
                            .newInstance();
        System.out.println(object.getClass());
        //自定义加载和系统加载的同一个类不认为相等
        System.out.println(object instanceof ClassLoaderTest);
    }
}
