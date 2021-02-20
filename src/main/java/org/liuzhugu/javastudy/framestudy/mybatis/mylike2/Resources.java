package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    public static Reader getResourceAsReader(String resource) throws IOException{
        return new InputStreamReader(getResourceAsSteam(resource));
    }

    private static InputStream getResourceAsSteam(String resource) throws IOException{
        //遍历所有的类加载器  尽可能去加载XML
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            //根据路径加载XML
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (inputStream != null) {
                return inputStream;
            }
        }
        throw new IOException("could not find resource " + resource);
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                //系统类加载器
                ClassLoader.getSystemClassLoader(),
                //当前线程正在使用类加载器
                Thread.currentThread().getContextClassLoader()
        };
    }
}
