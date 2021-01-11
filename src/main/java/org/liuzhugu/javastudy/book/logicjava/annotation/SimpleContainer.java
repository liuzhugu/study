package org.liuzhugu.javastudy.book.logicjava.annotation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Liuzhuzhu(info = {"简单的容器实现"})
public class SimpleContainer {

    //缓存   根据类型获取单例对象
    private static Map<Class<?>,Object> instances = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> cls) {
        try {
            //先判断是否是单例
            boolean singleton = cls.isAnnotationPresent(Singleton.class);
            if (singleton) {
                //单例从缓存中取
                Object obj = instances.get(cls);
                if (obj == null) {
                    //同步
                    synchronized (cls) {
                        //双重锁判定
                        obj = instances.get(cls);
                        if (obj == null) {
                            //缓存中也没有,那么创建一个缓存,在从缓存中返回
                            obj = createInstance(cls);
                            instances.put(cls,obj);
                        }
                    }
                }
                return (T)obj;
            } else {
                //否则直接创建一个返回
                return  createInstance(cls);
            }
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }

    //获取使用SingleInject注解的是什么字段,
    //然后创建一个相应的的实例set进去,那么就完成了注入
    private static <T> T createInstance(Class<T> cls) throws Exception{
        //创建对象
        T obj = cls.newInstance();

        //设置依赖关系,完成初始化
        //获取所有字段
        for (Field field : cls.getDeclaredFields()) {
            //判断是否使用了注解SingleInject
            if (field.isAnnotationPresent(SingleInject.class)) {
                //修改访问权限
                if (! field.isAccessible()) {
                    field.setAccessible(true);
                }
                //获取注解使用在什么类上
                Class<?> fieldCls = field.getType();
                //构造该类,设置依赖关系
                //因为依赖关系是多层的,所以递归处理
                field.set(obj,createInstance(fieldCls));
            }
        }
        return obj;
    }

    //单例的依赖注入




}
