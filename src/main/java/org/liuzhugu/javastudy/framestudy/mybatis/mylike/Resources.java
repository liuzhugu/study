package org.liuzhugu.javastudy.framestudy.mybatis.mylike;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    private static InputStream getResourceAsStream(String resource) throws IOException{
        //获取所有的类加载器  尽可能去遍历来加载resource
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            //根据路径加载XML
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (inputStream != null) {
                return inputStream;
            }
        }
        //找不到报错
        throw new IOException("could not find resource " + resource);
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[] {
                //系统类加载器
                ClassLoader.getSystemClassLoader(),
                //当前线程正在使用类加载器
                Thread.currentThread().getContextClassLoader()
        };
    }
}
