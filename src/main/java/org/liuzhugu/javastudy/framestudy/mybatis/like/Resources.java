package org.liuzhugu.javastudy.framestudy.mybatis.like;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    public static Reader getResourceAsReader(String resouce) throws IOException {
        return new InputStreamReader(getResourceAsStream(resouce));
    }

    private static InputStream getResourceAsStream(String resouce) throws IOException {
        //获取ClassLoader集合  最大限度搜索配置文件
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            //根据文件名加载文件
            InputStream inputStream = classLoader.getResourceAsStream(resouce);
            //获得配置文件之后立即返回
            if (inputStream != null) {
                return inputStream;
            }
        }
        //找不到报错
        throw new IOException("could not find resource " + resouce);
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                //系统类加载器
                ClassLoader.getSystemClassLoader(),
                //当前线程使用的ClassLoader
                Thread.currentThread().getContextClassLoader()
        };
    }

}
